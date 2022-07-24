package net.shyshkin.study.microservices.reactiveelasticqueryservice;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.UserConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.model.ElasticQueryServiceResponseModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.repository.TwitterElasticReactiveQueryRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "elastic-config.connection-url=http://${ELASTIC_HOST_ADDRESS}"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ContextConfiguration(initializers = ReactiveElasticQueryServiceApplicationTests.Initializer.class)
class ReactiveElasticQueryServiceApplicationTests {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserConfigData userConfigData;

    @Container
    static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer();

    @Autowired
    TwitterElasticReactiveQueryRepository repository;

    @Test
    @Order(10)
    void testInitCompleted() {

        //given
        TwitterIndexModel model = TwitterIndexModel.builder()
                .id("1")
                .userId(123L)
                .text("Some test text")
                .createdAt(ZonedDateTime.now())
                .build();
        repository.save(model).block();

        //when
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .until(
                        () -> repository.count().block(),
                        count -> count > 0
                );
        //then
        log.debug("Init completed. ElasticRepository has {} elements", repository.count().block());
    }

    @Test
    @Order(20)
    void getAllDocuments_ok() {

        //when
        var responseBodyFlux = webTestClient
                .get().uri("/documents")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(ElasticQueryServiceResponseModel.class)
                .getResponseBody();

        StepVerifier.create(responseBodyFlux)
                .consumeNextWith(model -> assertThat(model).isNotNull()
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", "1")
                        .hasFieldOrPropertyWithValue("text", "Some test text")
                )
                .verifyComplete();
    }

    @Test
    @Order(30)
    void getAllDocuments_ok_throughWebTestClient() {

        //when
        webTestClient.get().uri("/documents")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(ElasticQueryServiceResponseModel.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0))
                        .isNotNull()
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", "1")
                        .hasFieldOrPropertyWithValue("text", "Some test text")
                );
    }

    @Test
    @Order(40)
    void getAllDocuments_unauthorized_401() {

        //when
        webTestClient
                .get().uri("/documents")
                .exchange()

                //then
                .expectStatus().isUnauthorized();
    }

    @Test
    @Order(50)
    void getDocumentById_present() {

        //given
        String id = "1";

        //when
        webTestClient.get().uri("/documents/{id}", id)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(ElasticQueryServiceResponseModel.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0))
                        .isNotNull()
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", "1")
                        .hasFieldOrPropertyWithValue("text", "Some test text")
                );
    }

    @Test
    @Order(51)
    void getDocumentById_absent() {

        //given
        String id = "100";

        //when
        webTestClient.get().uri("/documents/{id}", id)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(ElasticQueryServiceResponseModel.class)
                .hasSize(0);
    }

    @Test
    @Order(60)
    void getDocumentByText_present() {

        //given
        String text = "some text";
        var requestModel = ElasticQueryServiceRequestModel.builder()
                .text(text)
                .build();

        //when
        webTestClient.post().uri("/documents/get-document-by-text")
                .bodyValue(requestModel)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(ElasticQueryServiceResponseModel.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0))
                        .isNotNull()
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", "1")
                        .hasFieldOrPropertyWithValue("text", "Some test text")
                );
    }

    @Test
    @Order(61)
    void getDocumentByText_absent() {

        //given
        String text = "absent";
        var requestModel = ElasticQueryServiceRequestModel.builder()
                .text(text)
                .build();

        //when
        webTestClient.post().uri("/documents/get-document-by-text")
                .bodyValue(requestModel)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(ElasticQueryServiceResponseModel.class)
                .hasSize(0);
    }

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String hostAddress = elasticsearchContainer.getHttpHostAddress();
            System.setProperty("ELASTIC_HOST_ADDRESS", hostAddress);
            log.debug("ELASTIC_HOST_ADDRESS: {}", hostAddress);
        }
    }
}