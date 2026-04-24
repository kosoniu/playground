package koson.playground.application.port.out;

import java.util.UUID;
import koson.playground.domain.valueobject.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Flux<Product> getAllProducts();
    Mono<Product> addProduct(Product product);
    Mono<Product> getProductById(UUID id);
    Mono<Void> deleteProductById(UUID id);

}
