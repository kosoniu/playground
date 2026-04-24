package koson.playground;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
@Slf4j
public class PlaygroundApplication {

    static {
        BlockHound.builder()
                .blockingMethodCallback((method) -> {
                    log.error("Blocking call detected: {}.{}", method.getClassName(), method.getName());
                })
                .install();
    }

    static void main(String[] args) {
        SpringApplication.run(PlaygroundApplication.class, args);
    }

}
