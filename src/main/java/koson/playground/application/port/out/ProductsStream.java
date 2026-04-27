package koson.playground.application.port.out;

import koson.playground.domain.valueobject.Product;
import reactor.core.publisher.Flux;

public interface ProductsStream {

    Flux<Product> stream();

}
