package koson.playground.application.service;

import java.util.UUID;
import koson.playground.adapter.out.persistence.entity.ProductEntity;
import koson.playground.adapter.out.persistence.repository.ProductCouchbaseRepository;
import koson.playground.application.port.out.ProductRepository;
import koson.playground.domain.valueobject.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductProvider implements ProductRepository {

    private final ProductCouchbaseRepository productCouchbaseRepository;

    @Override
    public Flux<Product> getAllProducts() {
        return this.productCouchbaseRepository.findAll()
                .map(productEntity -> new Product(
                                productEntity.id(),
                                productEntity.name(),
                                productEntity.price()
                        )
                );
    }

    @Override
    public Mono<Product> addProduct(Product product) {
        return this.productCouchbaseRepository.save(new ProductEntity(
                                null,
                                product.name(),
                                product.price()
                        )
                )
                .map(productEntity -> new Product(
                                productEntity.id(),
                                productEntity.name(),
                                productEntity.price()
                        )
                );
    }

    @Override
    public Mono<Product> getProductById(UUID id) {
        return this.productCouchbaseRepository.findById(id)
                .map(productEntity -> new Product(
                                productEntity.id(),
                                productEntity.name(),
                                productEntity.price()
                        )
                );
    }

    @Override
    public Mono<Void> deleteProductById(UUID id) {
        return this.productCouchbaseRepository.deleteById(id);
    }

}
