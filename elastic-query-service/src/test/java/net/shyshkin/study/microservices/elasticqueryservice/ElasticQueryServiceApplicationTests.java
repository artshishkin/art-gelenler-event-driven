package net.shyshkin.study.microservices.elasticqueryservice;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.UserConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elastic.query.client.service.ElasticQueryClient;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "elastic-config.connection-url=http://${ELASTIC_HOST_ADDRESS}"
})
@Testcontainers
@ContextConfiguration(initializers = ElasticQueryServiceApplicationTests.Initializer.class)
class ElasticQueryServiceApplicationTests {

    private static final ParameterizedTypeReference<List<ElasticQueryServiceResponseModel>> RESPONSE_MODEL_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserConfigData userConfigData;

    @MockBean
    ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    @Container
    static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer();

    @Test
    void getAllDocuments_ok() {

        //given
        given(elasticQueryClient.getAllIndexModels())
                .willReturn(List.of());

        //when
        var responseEntity = restTemplate
                .withBasicAuth(userConfigData.getUsername(), userConfigData.getPassword())
                .exchange("/documents", HttpMethod.GET, null, RESPONSE_MODEL_LIST_TYPE);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void getAllDocuments_unauthorized_401() {

        //when
        var responseEntity = restTemplate
                .exchange("/documents", HttpMethod.GET, null, RESPONSE_MODEL_LIST_TYPE);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getDocumentById_ok() {

        //given
        String id = "123";
        given(elasticQueryClient.getIndexModelById(anyString()))
                .willReturn(TwitterIndexModel.builder().id(id).build());

        //when
        var responseEntity = restTemplate
                .withBasicAuth(userConfigData.getUsername(), userConfigData.getPassword())
                .getForEntity("/documents/{id}", ElasticQueryServiceResponseModel.class, id);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id);
    }

    @Test
    void getDocumentsByText_ok() {

        //given
        String text = "some text to search";
        given(elasticQueryClient.getIndexModelByText(anyString()))
                .willReturn(List.of(TwitterIndexModel.builder().text(text).build()));

        var requestModel = ElasticQueryServiceRequestModel.builder()
                .text(text)
                .build();

        //when
        HttpEntity<ElasticQueryServiceRequestModel> reqEntity = new HttpEntity<>(requestModel);
        var responseEntity = restTemplate
                .withBasicAuth(userConfigData.getUsername(), userConfigData.getPassword())
                .exchange("/documents/get-document-by-text", HttpMethod.POST, reqEntity, RESPONSE_MODEL_LIST_TYPE);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .hasSize(1)
                .allSatisfy(model -> assertThat(model.getText()).isEqualTo(text));
    }

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String hostAddress = elasticsearchContainer.getHttpHostAddress();
            System.setProperty("ELASTIC_HOST_ADDRESS", hostAddress);
        }
    }

}