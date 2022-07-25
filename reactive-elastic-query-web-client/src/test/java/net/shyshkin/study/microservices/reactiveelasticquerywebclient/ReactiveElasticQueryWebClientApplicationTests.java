package net.shyshkin.study.microservices.reactiveelasticquerywebclient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import net.shyshkin.study.microservices.config.UserConfigData;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReactiveElasticQueryWebClientApplicationTests {

    @Autowired
    ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    @Autowired
    WebTestClient webClient;

    @Autowired
    UserConfigData userConfigData;

    @Test
    @Order(10)
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

    @Test
    @Order(20)
    void unauthorized() {

        //when
        webClient.get().uri("/query-by-text")
                .exchange()

                //then
                .expectStatus().isUnauthorized();
    }

    @Test
    @Order(30)
    void home() {

        //when
        webClient.get().uri("/")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    log.debug("Full html page: {}", html);
                    assertThat(html)
                            .contains("<title>Twitter Search Engine</title>")
                            .contains("<a class=\"nav-link\" href=\"/reactive-elastic-query-web-client/\">Main page</a>")
                            .contains("<a class=\"nav-link\" href=\"/reactive-elastic-query-web-client/home\">Search page</a>")
                            .contains("<form class=\"col-12\" method=\"post\" action=\"/reactive-elastic-query-web-client/logout\"><input type=\"hidden\" name=\"_csrf\" value=")
                            .contains("<input type=\"hidden\" name=\"_csrf\" value=\"")
                            .contains("<p>Hello <span>art</span><a href=\"/reactive-elastic-query-web-client/home\"> Let's get start searching!</a></p>")
                    ;
                });
    }


}