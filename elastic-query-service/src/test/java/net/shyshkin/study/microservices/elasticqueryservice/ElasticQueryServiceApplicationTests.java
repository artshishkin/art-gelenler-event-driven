package net.shyshkin.study.microservices.elasticqueryservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "elastic-config.connection-url=http://${ELASTIC_HOST_ADDRESS}"
})
@Testcontainers
@ContextConfiguration(initializers = ElasticQueryServiceApplicationTests.Initializer.class)
class ElasticQueryServiceApplicationTests {

    @Container
    static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer();

    @Test
    void contextLoads() {
    }

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String hostAddress = elasticsearchContainer.getHttpHostAddress();
            System.setProperty("ELASTIC_HOST_ADDRESS", hostAddress);
        }
    }

}