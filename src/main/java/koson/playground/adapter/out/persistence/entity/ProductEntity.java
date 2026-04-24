package koson.playground.adapter.out.persistence.entity;

import java.math.BigInteger;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

@Document
@Scope("product")
@Collection("products")
public record ProductEntity(
        @Id
        @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
        UUID id,
        String name,
        BigInteger price
) {

}
