package koson.playground.domain.service;

import java.util.UUID;
import koson.playground.application.port.out.ProductRepository;
import koson.playground.domain.model.PlaygroundCore;
import koson.playground.domain.valueobject.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaygroundProcessor implements PlaygroundCore {

    private final ProductRepository productRepository;

    @Override
    public Flux<Product> getAllProducts() {
        return this.productRepository.getAllProducts();
    }

    @Override
    public Mono<Product> addProduct(Product product) {
        return this.productRepository.addProduct(product);
    }

    @Override
    public Mono<Product> getProductById(UUID id) {
        return this.productRepository.getProductById(id);
    }

    @Override
    public Mono<Void> deleteProductById(UUID id) {
        return this.productRepository.deleteProductById(id);
    }
}
