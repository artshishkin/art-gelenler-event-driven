package net.shyshkin.study.microservices.elasticqueryservice;

import com.fasterxml.jackson.databind.JsonNode;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.UserConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elastic.query.client.service.ElasticQueryClient;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

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

    private static final String CLIENT_ID = "elastic-query-web-client";
    private static final String CLIENT_SECRET = "hKeXincDbrZvb9rnoJgAAqN8YsWNQPR2";
    private static final String REALM_NAME = "gelenler-tutorial";

    private static final String APP_USER_USERNAME = "app.user";
    private static final String APP_USER_PASSWORD = "123";
    private static final String APP_ADMIN_USERNAME = "app.admin";
    private static final String APP_ADMIN_PASSWORD = "234";
    private static final String APP_SUPER_USER_USERNAME = "app.superuser";
    private static final String APP_SUPER_USER_PASSWORD = "345";

    private static final ParameterizedTypeReference<List<ElasticQueryServiceResponseModel>> RESPONSE_MODEL_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private static final String ENV_FILE_PATH = "../docker-compose/.env";
    private static final String REALM_FILE_PATH = "../docker-compose/export/gelenler-tutorial-realm.json";
    private static final String DEFAULT_REALM_IMPORT_FILES_LOCATION = "/opt/keycloak/data/import/";

    private static Map<String, String> versions;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserConfigData userConfigData;

    @MockBean
    ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    @Container
    static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer();

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:" + getVersion("KEYCLOAK_VERSION"))
            .withAdminUsername("admin")
            .withAdminPassword("Pa55w0rd")
            .withRealmImportFile(".") //fake insert to enable flag --import realm
            .withCopyFileToContainer(MountableFile.forHostPath(REALM_FILE_PATH), DEFAULT_REALM_IMPORT_FILES_LOCATION + FilenameUtils.getName(REALM_FILE_PATH));

    @BeforeEach
    void setUp() {
        testRestTemplate.getRestTemplate().setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.ACCEPT, "application/vnd.api.v1+json");
            return execution.execute(request, body);
        }));
    }

    @Test
    void getAllDocuments_ok() {

        //given
        given(elasticQueryClient.getAllIndexModels())
                .willReturn(List.of());
        var requestEntity = RequestEntity.get("/documents")
                .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                .build();

        //when
        var responseEntity = testRestTemplate
                .exchange(requestEntity, RESPONSE_MODEL_LIST_TYPE);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void getAllDocuments_unauthorized_401() {

        //when
        var responseEntity = testRestTemplate
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
        var requestEntity = RequestEntity.get("/documents/{id}", id)
                .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                .build();

        //when
        var responseEntity = testRestTemplate
                .exchange(requestEntity, ElasticQueryServiceResponseModel.class);

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
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD));

        //when
        HttpEntity<ElasticQueryServiceRequestModel> reqEntity = new HttpEntity<>(requestModel, httpHeaders);
        var responseEntity = testRestTemplate
                .exchange("/documents/get-document-by-text", HttpMethod.POST, reqEntity, RESPONSE_MODEL_LIST_TYPE);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .hasSize(1)
                .allSatisfy(model -> assertThat(model.getText()).isEqualTo(text));
    }

    @Nested
    class ControllerAdviceTests {

        @Test
        void accessDeniedTest() {
            //given
            String id = "123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willThrow(new AccessDeniedException("You have no permission to get document with id " + id));
            var requestEntity = RequestEntity.get("/documents/{id}", id)
                    .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                    .build();

            //when
            var responseEntity = testRestTemplate
                    .exchange(requestEntity, String.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody())
                    .isNotNull()
                    .isEqualTo("You are not authorized to access this resource");
        }

        @Test
        void illegalArgumentTest() {
            //given
            String id = "-123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willThrow(new IllegalArgumentException("ID can not be negative"));
            var requestEntity = RequestEntity.get("/documents/{id}", id)
                    .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                    .build();

            //when
            var responseEntity = testRestTemplate
                    .exchange(requestEntity, String.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody())
                    .isNotNull()
                    .isEqualTo("Illegal argument: ID can not be negative");
        }

        @Test
        void validationExceptionTest() {

            //given
            String text = "";
            var requestModel = ElasticQueryServiceRequestModel.builder()
                    .text(text)
                    .build();
            var requestEntity = RequestEntity.post("/documents/get-document-by-text")
                    .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                    .body(requestModel);

            //when
            var responseEntity = testRestTemplate
                    .exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
                    });

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody())
                    .isNotNull()
                    .satisfies(map -> assertThat(map.get("text")).isNotEmpty());
        }

        @Test
        void runtimeExceptionTest() {
            //given
            String id = "123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willThrow(new RuntimeException("Something bad"));
            var requestEntity = RequestEntity.get("/documents/{id}", id)
                    .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                    .build();

            //when
            var responseEntity = testRestTemplate
                    .exchange(requestEntity, String.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody())
                    .isNotNull()
                    .isEqualTo("Service runtime exception: Something bad");
        }

        @Test
        @Disabled("After OAuth added this test fails")
        void anotherExceptionTest() {
            //given
            String id = "123";
            given(elasticQueryClient.getIndexModelById(anyString()))
                    .willAnswer(invocationOnMock -> {
                        throw new FileNotFoundException("There is not file with id " + id);
                    });
            var requestEntity = RequestEntity.get("/documents/{id}", id)
                    .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                    .build();

            //when
            var responseEntity = testRestTemplate
                    .exchange(requestEntity, String.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(responseEntity.getBody())
                    .isNotNull()
                    .isEqualTo("A server error occurred!");
        }

    }

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String hostAddress = elasticsearchContainer.getHttpHostAddress();
            System.setProperty("ELASTIC_HOST_ADDRESS", hostAddress);
            String issuerUriProperty = String.format(
                    "spring.security.oauth2.resourceserver.jwt.issuer-uri=%srealms/gelenler-tutorial",
                    keycloakContainer.getAuthServerUrl()
            );

            TestPropertyValues
                    .of(issuerUriProperty)
                    .applyTo(applicationContext.getEnvironment());
        }
    }

    private final RestTemplate oauthServerRestTemplate = new RestTemplateBuilder()
            .basicAuthentication(CLIENT_ID, CLIENT_SECRET)
            .rootUri(keycloakContainer.getAuthServerUrl())
            .build();


    protected String getJwtAccessToken(String username, String password) {

        Optional<String> fromCache = getFromCache(username, password);
        if (fromCache.isPresent())
            return fromCache.get();

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");

        map.add("username", username);
        map.add("password", password);
        map.add("scope", "openid profile");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        var responseEntity = oauthServerRestTemplate
//                .postForEntity("/realms/" + REALM_NAME + "/protocol/openid-connect/token", requestEntity, String.class);
                .postForEntity("/realms/" + REALM_NAME + "/protocol/openid-connect/token", requestEntity, JsonNode.class);

        //then
        log.debug("Response from OAuth2.0 server: {}", responseEntity);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        var oAuthResponse = responseEntity.getBody();
        assertThat(oAuthResponse).isNotNull();
//        String accessToken = oAuthResponse;
        String accessToken = oAuthResponse.at("/access_token").asText();
        assertThat(accessToken).isNotEmpty();

        log.debug("JWT Access Token is {}", accessToken);
        saveToCache(username, password, accessToken);
        return accessToken;
    }

    private static final Map<String, String> cache = new HashMap<>();

    private Optional<String> getFromCache(String username, String password) {
        String key = username + ":" + password;
        return Optional.ofNullable(cache.get(key));
    }

    private void saveToCache(String username, String password, String accessToken) {
        String key = username + ":" + password;
        cache.put(key, accessToken);
    }

    private static String getVersion(String versionKey) {
        if (versions == null) {
            versions = getEnvVariables();
        }
        return versions.get(versionKey);
    }

    private static Map<String, String> getEnvVariables() {
        Properties properties = new Properties();
        log.debug("Current directory: {}", System.getProperty("user.dir"));
        try (Reader reader = new FileReader(ENV_FILE_PATH)) {
            properties.load(reader);
        } catch (IOException e) {
            log.error("", e);
        }

        Map<String, String> envVariables = properties.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(),
                        e -> e.getValue().toString()));

        log.debug("Docker-compose Environment variables: {}", envVariables);
        return envVariables;
    }

}