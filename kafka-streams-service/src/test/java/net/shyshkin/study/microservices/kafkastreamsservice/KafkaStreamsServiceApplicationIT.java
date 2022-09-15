package net.shyshkin.study.microservices.kafkastreamsservice;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.kafka.client.KafkaAdminClient;
import net.shyshkin.study.microservices.kafkastreamsservice.model.KafkaStreamsResponseModel;
import net.shyshkin.study.microservices.test.KeycloakAbstractTest;
import net.shyshkin.study.microservices.util.VersionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
@Testcontainers
@Disabled
class KafkaStreamsServiceApplicationIT extends KeycloakAbstractTest {

    private static final String CLIENT_ID = "elastic-query-service";
    private static final String CLIENT_SECRET = "ev8wdPngiAmJTwIKlY94kBGC5Vxfluo7";
    private static final String REALM_NAME = "gelenler-tutorial";

    private static final String APP_USER_USERNAME = "app.user";
    private static final String APP_USER_PASSWORD = "123";
    private static final String APP_ADMIN_USERNAME = "app.admin";
    private static final String APP_ADMIN_PASSWORD = "234";
    private static final String APP_SUPER_USER_USERNAME = "app.superuser";
    private static final String APP_SUPER_USER_PASSWORD = "345";

    @Autowired
    TestRestTemplate testRestTemplate;

    @MockBean
    KafkaAdminClient kafkaAdminClient;

    static KafkaContainer kafkaContainer;

    static {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag(VersionUtil.getVersion("KAFKA_VERSION")))
                .withReuse(true)
                .withStartupTimeout(Duration.ofMinutes(3));
        kafkaContainer.start();
    }

    @BeforeEach
    void setUp() {
        testRestTemplate.getRestTemplate().setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.ACCEPT, "application/vnd.api.v1+json");
            return execution.execute(request, body);
        }));
    }

    @Test
    void getWordCountByWord_ok() {

        //given
        String word = "java";
        var requestEntity = RequestEntity.get("/get-word-count-by-word/{word}", word)
                .headers(h -> h.setBearerAuth(getJwtAccessToken(APP_USER_USERNAME, APP_USER_PASSWORD)))
                .build();

        //when
        var responseEntity = testRestTemplate
                .exchange(requestEntity, KafkaStreamsResponseModel.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void getWordCountByWord_withoutAuthHeader_unauthorized_401() {

        //given
        String word = "java";

        //when
        var responseEntity = testRestTemplate
                .getForEntity("/get-word-count-by-word/{word}", KafkaStreamsResponseModel.class, word);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
        assertThat(responseEntity.getHeaders().getFirst("WWW-Authenticate"))
                .isEqualTo("Bearer");
    }

    @Test
    void getWordCountByWord_withExpiredToken_thenUnauthorized_401() {

        //given
        String word = "java";
        String expiredAccessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICItWmI0NFJ3dW9Eb1hrM3c5SzR0bG1aN1Zad29NMmpKazBackVvc0V3TDFJIn0.eyJleHAiOjE2NjMyMjY2ODMsImlhdCI6MTY2MzIyNjM4MywianRpIjoiNDcwMzA0MjctYmUzYy00NDJmLTlmZDYtN2I2MGZiYmZlNmI5IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgxL3JlYWxtcy9nZWxlbmxlci10dXRvcmlhbCIsImF1ZCI6WyJrYWZrYS1zdHJlYW1zLXNlcnZpY2UiLCJhbmFsaXRpY3Mtc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiNzgxZjJmMDgtMTYzMy00ZjBjLTg3YzctMTY2MmE0YWQwNDhiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZWxhc3RpYy1xdWVyeS1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImNiYTdmNjBlLTY3YTEtNGNjYi04ZWFlLTBhOTFhMTRmZjIyOSIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1nZWxlbmxlci10dXRvcmlhbCIsImFwcF9hZG1pbl9yb2xlIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgYXBwX2FkbWluX3JvbGUgZW1haWwiLCJzaWQiOiJjYmE3ZjYwZS02N2ExLTRjY2ItOGVhZS0wYTkxYTE0ZmYyMjkiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJBcHBLYXRlIEFkbWluIiwiZ3JvdXBzIjpbImFwcF9hZG1pbl9ncm91cCJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhcHAuYWRtaW4iLCJnaXZlbl9uYW1lIjoiQXBwS2F0ZSIsImZhbWlseV9uYW1lIjoiQWRtaW4iLCJlbWFpbCI6ImFwcC5hZG1pbkBnbWFpbC5jb20ifQ.WhJhfbV1vcCtE56V3Ib7J-9dPmKZ2-vh6eR8mvu_zX2dGUlidzDSD628elXRQ7e0zYA3lm2UT-wPjZ_5ilygiUyEM_KeykdAGu86CZUaRWf-wKahj4IhVNNQqfZ-93qoNiH8MKgUhKwExf7dT5Z0fYMPYYKajPdGf3_Jgv-4vvRS2O59gkELewpHm1l4MAn_cdT0BH3SSMWMrNU4aGV0OLbJrrwPM1HT8cyVPWoSkyrYDjn6M7Iq0-lp3-kteTlfHAvP5UGzWOMwDCsIEUCrSvvTCRGk53VJe6iizqKsnx9CgpIC7Un0-6KxHHvL4ezk_EUvJGwKfP1MT3wwONGm-g";
        var requestEntity = RequestEntity.get("/get-word-count-by-word/{word}", word)
                .headers(h -> h.setBearerAuth(expiredAccessToken))
                .build();

        //when
        var responseEntity = testRestTemplate
                .exchange(requestEntity, KafkaStreamsResponseModel.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
        assertThat(responseEntity.getHeaders().getFirst("WWW-Authenticate"))
                .contains("An error occurred while attempting to decode the Jwt: Jwt expired at");
    }

    @Test
    void getWordCountByWord_withWrongAudience_thenUnauthorized_401() {

        //given
        String word = "java";
        var requestEntity = RequestEntity.get("/get-word-count-by-word/{word}", word)
                .headers(h -> h.setBearerAuth(getJwtAccessTokenWithWrongAudience(APP_USER_USERNAME, APP_USER_PASSWORD)))
                .build();

        //when
        var responseEntity = testRestTemplate
                .exchange(requestEntity, KafkaStreamsResponseModel.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
        assertThat(responseEntity.getHeaders().getFirst("WWW-Authenticate"))
                .contains("An error occurred while attempting to decode the Jwt: The required audience kafka-streams-service is missing");
    }

    @DynamicPropertySource
    static void appProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> String.format("%srealms/%s", keycloakContainer.getAuthServerUrl(), REALM_NAME)
        );

        registry.add(
                "kafka-config.bootstrap-servers",
                kafkaContainer::getBootstrapServers
        );
    }

    private final RestTemplate oauthServerRestTemplate = new RestTemplateBuilder()
            .basicAuthentication(CLIENT_ID, CLIENT_SECRET)
            .rootUri(keycloakContainer.getAuthServerUrl())
            .build();

    protected String getJwtAccessToken(String username, String password) {
        return getJwtAccessToken(username, password, oauthServerRestTemplate);
    }

    protected String getJwtAccessTokenWithWrongAudience(String username, String password) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication("elastic-query-web-client", "hKeXincDbrZvb9rnoJgAAqN8YsWNQPR2")
                .rootUri(keycloakContainer.getAuthServerUrl())
                .build();
        return getJwtAccessToken(username, password, restTemplate);
    }

    protected String getJwtAccessToken(String username, String password, RestTemplate restTemplate) {

        Optional<String> fromCache = getFromCache(username, password, restTemplate);
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

        var responseEntity = restTemplate
                .postForEntity("/realms/" + REALM_NAME + "/protocol/openid-connect/token", requestEntity, JsonNode.class);

        //then
        log.debug("Response from OAuth2.0 server: {}", responseEntity);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        var oAuthResponse = responseEntity.getBody();
        assertThat(oAuthResponse).isNotNull();
        String accessToken = oAuthResponse.at("/access_token").asText();
        assertThat(accessToken).isNotEmpty();

        log.debug("JWT Access Token is {}", accessToken);
        saveToCache(username, password, restTemplate, accessToken);
        return accessToken;
    }

    private static final Map<String, String> cache = new HashMap<>();

    private Optional<String> getFromCache(String username, String password, RestTemplate restTemplate) {
        String key = username + ":" + password + restTemplate.hashCode();
        return Optional.ofNullable(cache.get(key));
    }

    private void saveToCache(String username, String password, RestTemplate restTemplate, String accessToken) {
        String key = username + ":" + password + restTemplate.hashCode();
        cache.put(key, accessToken);
    }
}