package net.shyshkin.study.microservices.reactiveelasticqueryservice;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.UserConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.business.ReactiveElasticQueryClient;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.repository.TwitterElasticReactiveQueryRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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

    @SpyBean
    ReactiveElasticQueryClient<TwitterIndexModel> elasticQueryClient;

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

    @Nested
    class ControllerAdviceTests {

        @Test
        void accessDeniedTest_throwsException() {
            //given
            String id = "123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willThrow(new AccessDeniedException("You have no permission to get document with id " + id));

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().isForbidden()
                    .expectBody(String.class)
                    .isEqualTo("You are not authorized to access this resource");
        }

        @Test
        void accessDeniedTest_monoError() {

            //given
            String id = "123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willReturn(Mono.error(() -> new AccessDeniedException("You have no permission to get document with id " + id)));

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().isForbidden()
                    .expectBody(String.class)
                    .isEqualTo("You are not authorized to access this resource");
        }

        @Test
        void illegalArgumentTest_throwsException() {
            //given
            String id = "-123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willThrow(new IllegalArgumentException("ID can not be negative"));

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .isEqualTo("Illegal argument: ID can not be negative");
        }

        @Test
        void illegalArgumentTest_monoError() {
            //given
            String id = "-123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willReturn(Mono.error(() -> new IllegalArgumentException("ID can not be negative")));

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .isEqualTo("Illegal argument: ID can not be negative");
        }

        @Test
        void validationExceptionTest() {
            //given
            String text = "";
            var requestModel = ElasticQueryServiceRequestModel.builder()
                    .text(text)
                    .build();

            //when
            webTestClient.post().uri("/documents/get-document-by-text")
                    .bodyValue(requestModel)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().isBadRequest()
                    .expectBody(new ParameterizedTypeReference<Map<String, String>>() {
                    })
                    .value(map -> {
                        log.debug("Response Map: {}", map);
                        assertThat(map.get("text")).isNotEmpty();
                    });
        }

        @Test
        void runtimeExceptionTest_throwException() {
            //given
            String id = "-123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willThrow(new RuntimeException("Something bad"));

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .isEqualTo("Service runtime exception: Something bad");
        }

        @Test
        void runtimeExceptionTest_monoError() {
            //given
            String id = "-123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willReturn(Mono.error(() -> new RuntimeException("Something bad")));

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .isEqualTo("Service runtime exception: Something bad");
        }

        @Test
        void anotherExceptionTest_throwException() {
            //given
            String id = "-123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willAnswer(invocationOnMock -> {
                        throw new FileNotFoundException("There is not file with id " + id);
                    });

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().is5xxServerError()
                    .expectBody(String.class)
                    .isEqualTo("A server error occurred!");
        }

        @Test
        void anotherExceptionTest_monoError() {
            //given
            String id = "-123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willReturn(Mono.error(() -> new FileNotFoundException("There is not file with id " + id)));

            //when
            webTestClient.get().uri("/documents/{id}", id)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                    .exchange()

                    //then
                    .expectStatus().is5xxServerError()
                    .expectBody(String.class)
                    .isEqualTo("A server error occurred!");
        }

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