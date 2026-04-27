package koson.playground.adapter.in.persistence.dcp;

import com.couchbase.client.dcp.Client;
import com.couchbase.client.dcp.StreamFrom;
import com.couchbase.client.dcp.StreamTo;
import com.couchbase.client.dcp.message.DcpDeletionMessage;
import com.couchbase.client.dcp.message.DcpMutationMessage;
import com.couchbase.client.dcp.message.MessageUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import koson.playground.adapter.out.persistence.entity.ProductEntity;
import koson.playground.application.port.in.ProductsSynchronizer;
import koson.playground.domain.valueobject.Product;
import koson.playground.infrastructure.properties.CouchbaseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductChangeStreamService {

    private Client client;
    private final CouchbaseProperties couchbaseProperties;
    private final ProductsSynchronizer productsSynchronizer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @PostConstruct
    public void startListening() {
        Mono.fromCallable(() -> Client.builder()
                        .connectionString(couchbaseProperties.connectionString())
                        .credentials(
                                this.couchbaseProperties.username(),
                                this.couchbaseProperties.password()
                        )
                        .bucket(couchbaseProperties.bucketName())
                        .securityConfig(security -> security.enableTls(true))
                        .collectionsAware(true)
                        .collectionNames("product.products")
                        .build()
                )
                .map(client -> {
                    this.client = client;

                    client.controlEventHandler((flowController, event) -> {
                        log.info("Received DCP control event: {}", MessageUtil.humanize(event));
                        flowController.ack(event);
                        event.release();
                    });

                    client.dataEventHandler((flowController, event) -> {
                        try {
                            String key = MessageUtil.getKeyAsString(event);
                            key = key.replaceAll("\\s", "");
                            log.info("Received DCP data event: {}", MessageUtil.humanize(event));
                            if (DcpMutationMessage.is(event)) {
                                String content = DcpMutationMessage.content(event).toString(StandardCharsets.UTF_8);
                                log.info("MUTATION key={} content={}", key, content);
                            } else if (DcpDeletionMessage.is(event)) {
                                log.info("DELETION key={}", MessageUtil.getKeyAsString(event));
                            }

                            try {
                                var entity = this.objectMapper.readValue(
                                        MessageUtil.getContent(event).toString(StandardCharsets.UTF_8),
                                        ProductEntity.class
                                );

                                this.productsSynchronizer.synchronize(
                                        new Product(
                                                UUID.fromString(key),
                                                entity.name(),
                                                entity.price()
                                        )
                                );
                            } catch (JacksonException e) {
                                this.productsSynchronizer.synchronize(new Product(UUID.fromString(key), null, null));
                            }

                        } finally {
                            flowController.ack(event);
                            event.release();
                        }
                    });

                    return client;
                })
                .flatMap(Client::connect)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(result -> log.info("Connected to Couchbase DCP stream"))
                .then(Mono.defer(() -> client.initializeState(StreamFrom.NOW, StreamTo.INFINITY)))
                .then(Mono.defer(() -> client.startStreaming()))
                .retry(3)
                .subscribe(
                        result -> log.info("DCP streaming started for bucket=products scope=product"),
                        error -> log.error("Failed to start DCP streaming", error)
                );
    }

    @PreDestroy
    public void stop() {
        if (client != null) {
            client.disconnect().block(Duration.ofSeconds(10));
        }
    }

}
