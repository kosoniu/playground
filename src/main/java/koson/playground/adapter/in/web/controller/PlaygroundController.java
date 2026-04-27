package koson.playground.adapter.in.web.controller;

import java.util.UUID;
import koson.playground.adapter.in.web.dto.ProductRequest;
import koson.playground.adapter.in.web.dto.ProductResponse;
import koson.playground.application.port.in.PlaygroundPort;
import koson.playground.application.port.out.ProductsStream;
import koson.playground.domain.model.PlaygroundCore;
import koson.playground.domain.valueobject.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PlaygroundController implements PlaygroundPort {

    private final PlaygroundCore playgroundCore;
    private final ProductsStream productsStream;

    @Override
    public Mono<String> hello() {
        return Mono.just("Hello World!");
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return this.playgroundCore.getAllProducts()
                .doOnNext(product -> log.info("Product: {}", product))
                .map(product -> new ProductResponse(
                                product.id(),
                                product.name(),
                                product.price()
                        )
                )
                .doOnNext(productResponse -> log.info("ProductResponse: {}", productResponse));
    }

    @Override
    public Mono<ProductResponse> addProduct(ProductRequest productRequest) {
        return this.playgroundCore.addProduct(new Product(
                                null,
                                productRequest.name(),
                                productRequest.price()
                        )
                )
                .doOnSubscribe(subscription -> log.info("Adding product: {}", productRequest))
                .doOnNext(product -> log.info("Added Product: {}", product))
                .map(product -> new ProductResponse(
                                product.id(),
                                product.name(),
                                product.price()
                        )
                )
                .doOnNext(productResponse -> log.info("Added ProductResponse: {}", productResponse));
    }

    @Override
    public Mono<ProductResponse> getProductById(UUID id) {
        return this.playgroundCore.getProductById(id)
                .doOnSubscribe(subscription -> log.info("Getting product by id: {}", id))
                .doOnNext(product -> log.info("Got Product: {}", product))
                .map(product -> new ProductResponse(
                                product.id(),
                                product.name(),
                                product.price()
                        )
                )
                .doOnNext(productResponse -> log.info("Got ProductResponse: {}", productResponse));
    }

    @Override
    public Mono<Void> deleteProductById(UUID id) {
        return this.playgroundCore.deleteProductById(id)
                .doOnSubscribe(subscription -> log.info("Deleting product by id: {}", id))
                .doOnSuccess(unused -> log.info("Deleted product with id: {}", id));
    }

    @Override
    public Flux<ProductResponse> productsStream() {
        return this.productsStream.stream()
                .doOnNext(product -> log.info("Streaming Product: {}", product))
                .map(product -> new ProductResponse(
                                product.id(),
                                product.name(),
                                product.price()
                        )
                )
                .doOnNext(productResponse -> log.info("Streaming ProductResponse: {}", productResponse));
    }

}
