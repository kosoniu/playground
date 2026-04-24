package koson.playground.domain.model;

import java.util.UUID;
import koson.playground.domain.valueobject.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlaygroundCore {

    Flux<Product> getAllProducts();
    Mono<Product> addProduct(Product product);
    Mono<Product> getProductById(UUID id);
    Mono<Void> deleteProductById(UUID id);

}
