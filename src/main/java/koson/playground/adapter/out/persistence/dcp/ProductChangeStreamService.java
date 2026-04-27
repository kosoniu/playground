package koson.playground.adapter.out.persistence.dcp;

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
import koson.playground.infrastructure.properties.CouchbaseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductChangeStreamService {

    private Client client;

    private final CouchbaseProperties couchbaseProperties;

    @PostConstruct
    @Async
    public void startListening() {
        client = Client.builder()
                .connectionString(couchbaseProperties.connectionString())
                .credentials(
                        this.couchbaseProperties.username(),
                        this.couchbaseProperties.password()
                )
                .bucket(couchbaseProperties.bucketName())
                .securityConfig(security -> security.enableTls(true))
                .collectionsAware(true)
                .collectionNames("product.products")
                .build();

        client.controlEventHandler((flowController, event) -> {
            flowController.ack(event);
            event.release();
        });

        client.dataEventHandler((flowController, event) -> {
            try {
                if (DcpMutationMessage.is(event)) {
                    String key = MessageUtil.getKeyAsString(event);
                    String content = DcpMutationMessage.content(event).toString(StandardCharsets.UTF_8);
                    log.info("MUTATION key={} content={}", key, content);
                } else if (DcpDeletionMessage.is(event)) {
                    log.info("DELETION key={}", MessageUtil.getKeyAsString(event));
                }
            } finally {
                flowController.ack(event);
                event.release();
            }
        });

        client.connect().block(Duration.ofSeconds(30));
        client.initializeState(StreamFrom.NOW, StreamTo.INFINITY).block();
        client.startStreaming().block();

        log.info("DCP streaming started for bucket=products scope=product");
    }

    @PreDestroy
    public void stop() {
        if (client != null) {
            client.disconnect().block(Duration.ofSeconds(10));
        }
    }

}
