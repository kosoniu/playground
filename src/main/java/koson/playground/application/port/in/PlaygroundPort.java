package koson.playground.application.port.in;

import java.util.UUID;
import koson.playground.adapter.in.web.dto.ProductRequest;
import koson.playground.adapter.in.web.dto.ProductResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/playground")
public interface PlaygroundPort {

    @GetMapping("/hello")
    Mono<String> hello();

    @GetMapping("/products")
    Flux<ProductResponse> getAllProducts();

    @PostMapping("/products")
    Mono<ProductResponse> addProduct(@RequestBody ProductRequest productRequest);

    @GetMapping("/products/{id}")
    Mono<ProductResponse> getProductById(@PathVariable UUID id);

    @DeleteMapping("/products/{id}")
    Mono<Void> deleteProductById(@PathVariable UUID id);

    @GetMapping(value = "/products/stream", produces = "text/event-stream")
    Flux<ProductResponse> productsStream();

}
