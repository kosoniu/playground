package koson.playground.application.service;

import koson.playground.application.port.in.ProductsSynchronizer;
import koson.playground.application.port.out.ProductsStream;
import koson.playground.domain.valueobject.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductsStreamer implements ProductsSynchronizer, ProductsStream {

    private final Sinks.Many<Product> productSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void synchronize(Product product) {
        this.productSink.tryEmitNext(product).orThrow();
    }

    @Override
    public Flux<Product> stream() {
        return this.productSink.asFlux();
    }

}
