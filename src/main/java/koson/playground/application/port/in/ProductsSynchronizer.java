package koson.playground.application.port.in;

import koson.playground.domain.valueobject.Product;
import reactor.core.publisher.Mono;

public interface ProductsSynchronizer {

    void synchronize(Product product);

}
