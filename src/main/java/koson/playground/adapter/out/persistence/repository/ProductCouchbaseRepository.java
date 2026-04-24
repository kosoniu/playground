package koson.playground.adapter.out.persistence.repository;

import java.util.UUID;
import koson.playground.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductCouchbaseRepository extends ReactiveCrudRepository<ProductEntity, UUID> {

}
