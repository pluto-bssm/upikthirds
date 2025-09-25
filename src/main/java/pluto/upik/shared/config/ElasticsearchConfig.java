package pluto.upik.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "pluto.upik.domain.guide.repository")
@PropertySource("classpath:elasticsearch.properties")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.rest.uris}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.rest.connection-timeout:1s}")
    private String connectionTimeout;

    @Value("${spring.elasticsearch.rest.read-timeout:30s}")
    private String readTimeout;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUri.replace("http://", ""))
                .withConnectTimeout(Duration.parse("PT" + connectionTimeout.replace("s", "S")))
                .withSocketTimeout(Duration.parse("PT" + readTimeout.replace("s", "S")))
                .build();
    }
}