package koson.playground.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "couchbase")
public record CouchbaseProperties(
        String connectionString,
        String username,
        String password,
        String bucketName
) {

}
