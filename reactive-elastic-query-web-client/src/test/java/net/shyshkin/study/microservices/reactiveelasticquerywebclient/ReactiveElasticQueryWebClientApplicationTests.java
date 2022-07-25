package net.shyshkin.study.microservices.reactiveelasticquerywebclient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
@ActiveProfiles("local")
class ReactiveElasticQueryWebClientApplicationTests {

    @Autowired
    ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    @Test
    void contextLoads() {
        log.debug("Elastic Query Web Client Config Data: {}", elasticQueryWebClientConfigData);
        assertThat(elasticQueryWebClientConfigData)
                .isNotNull()
                .satisfies(data -> assertThat(data.getWebclient())
                        .isNotNull()
                        .satisfies(webclientData -> assertAll(
                                () -> assertThat(webclientData.getConnectTimeoutMs()).isPositive(),
                                () -> assertThat(webclientData.getReadTimeoutMs()).isPositive(),
                                () -> assertThat(webclientData.getWriteTimeoutMs()).isPositive(),
                                () -> assertThat(webclientData.getMaxInMemorySize()).isPositive(),
                                () -> assertThat(webclientData.getContentType()).contains("application", "json"),
                                () -> assertThat(webclientData.getAcceptType()).isEqualTo("text/event-stream"),
                                () -> assertThat(webclientData.getBaseUrl())
                                        .hasPath("/reactive-elastic-query-service/documents")
                        )));
    }

}