package net.shyshkin.study.microservices.elasticquerywebclient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
class ElasticQueryWebClientApplicationTests {

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
                                () -> assertThat(webclientData.getAcceptType()).contains("application", "json"),
                                () -> assertThat(webclientData.getBaseUrl())
                                        .hasPath("/elastic-query-service/documents")
                        )));
    }
}