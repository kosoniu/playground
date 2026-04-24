package koson.playground.infrastructure.config;

import com.couchbase.client.java.env.ClusterEnvironment;
import koson.playground.infrastructure.properties.CouchbaseProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;

@Configuration
@EnableConfigurationProperties(CouchbaseProperties.class)
@EnableReactiveCouchbaseRepositories(basePackages = { "koson.playground.adapter.out.persistence" })
@RequiredArgsConstructor
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    private final CouchbaseProperties couchbaseProperties;

    @Override
    public String getConnectionString() {
        return this.couchbaseProperties.connectionString();
    }

    @Override
    public String getUserName() {
        return this.couchbaseProperties.username();
    }

    @Override
    public String getPassword() {
        return this.couchbaseProperties.password();
    }

    @Override
    public String getBucketName() {
        return this.couchbaseProperties.bucketName();
    }

    @Override
    protected void configureEnvironment(ClusterEnvironment.Builder builder) {
        builder
                .securityConfig(security -> security
                        .enableTls(true)
                );
    }

}
