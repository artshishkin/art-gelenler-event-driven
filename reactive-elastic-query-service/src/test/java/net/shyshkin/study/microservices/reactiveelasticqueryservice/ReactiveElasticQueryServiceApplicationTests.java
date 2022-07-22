package net.shyshkin.study.microservices.reactiveelasticqueryservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
@Disabled("Can not run without Elasticsearch cluster")
class ReactiveElasticQueryServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}