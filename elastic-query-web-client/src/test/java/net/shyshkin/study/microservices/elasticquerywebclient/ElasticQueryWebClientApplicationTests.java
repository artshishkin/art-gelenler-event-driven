package net.shyshkin.study.microservices.elasticquerywebclient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import net.shyshkin.study.microservices.config.UserConfigData;
import net.shyshkin.study.microservices.elasticquerywebclient.service.ElasticWebClient;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientResponseModel;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ElasticQueryWebClientApplicationTests {

    @Autowired
    ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    @Autowired
    WebTestClient webClient;

    @Autowired
    UserConfigData userConfigData;

    @MockBean
    ElasticWebClient elasticWebClient;

    static String csrfValue;
    static LinkedMultiValueMap<String, String> cookiesWithSession;

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
                                () -> assertThat(webclientData.getAcceptType()).contains("application", "json"),
                                () -> assertThat(webclientData.getBaseUrl())
                                        .hasPath("/elastic-query-service/documents")
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
                            .contains("<a class=\"nav-link\" href=\"/elastic-query-web-client/\">Main page</a>")
                            .contains("<a class=\"nav-link\" href=\"/elastic-query-web-client/home\">Search page</a>")
                            .contains("<form class=\"col-12\" method=\"post\" action=\"/elastic-query-web-client/logout\"><input type=\"hidden\" name=\"_csrf\" value=")
                            .contains("<input type=\"hidden\" name=\"_csrf\" value=\"")
                            .contains("<p>Hello <span>art</span><a href=\"/elastic-query-web-client/home\"> Let's get start searching!</a></p>")
                    ;
                });
    }

    @Test
    @Order(40)
    void searchPage() {

        //when
        webClient.get().uri("/home")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectCookie().value("JSESSIONID", value -> {
                    log.debug("Session cookie: {}", value);
                    cookiesWithSession = new LinkedMultiValueMap<>();
                    cookiesWithSession.add("JSESSIONID", value);
                })
                .expectBody(String.class)
                .value(htmlContent -> {
                    log.debug("Full html page: {}", htmlContent);
                    assertThat(htmlContent)
                            .contains("<title>Twitter Search Engine</title>")
                            .contains("<a class=\"nav-link\" href=\"/elastic-query-web-client/\">Main page</a>")
                            .contains("<a class=\"nav-link\" href=\"/elastic-query-web-client/home\">Search page</a>")
                            .contains("<input type=\"hidden\" name=\"_csrf\" value=\"")
                            .contains("<input class=\"form-control\" type=\"text\" id=\"text\" placeholder=\"Enter text to search\" name=\"text\" value=\"\">")
                            .contains("<input class=\"btn btn-dark\" type=\"submit\" value=\"Search\">")
                    ;
                    int formIndex = htmlContent.indexOf("action=\"/elastic-query-web-client/query-by-text\"");
                    int csrfIndex = htmlContent.indexOf("name=\"_csrf\"", formIndex);
                    int valueIndex = htmlContent.indexOf("value=", csrfIndex);
                    int csrfStart = htmlContent.indexOf("\"", valueIndex) + 1;
                    int csrfStop = htmlContent.indexOf("\"", csrfStart);
                    csrfValue = htmlContent.substring(csrfStart, csrfStop);
                    log.debug("CSRF: {}", csrfValue);
                });
    }

    @Test
    @Order(50)
    void getDocumentByText() {

        //given
        String searchText = "test";
        var expectedRequestModel = ElasticQueryWebClientRequestModel.builder()
                .text(searchText)
                .build();
        ElasticQueryWebClientResponseModel expectedResponseModel = ElasticQueryWebClientResponseModel.builder()
                .text("Some " + searchText + " text")
                .id("123")
                .userId(321L)
                .createdAt(ZonedDateTime.now().minusDays(1))
                .build();
        given(elasticWebClient.getDataByText(any(ElasticQueryWebClientRequestModel.class)))
                .willReturn(List.of(expectedResponseModel));

        //when

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("_csrf", csrfValue); //from previous test
        formData.add("text", searchText);

        webClient.post().uri("/query-by-text")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .cookies(cookies -> cookies.addAll(cookiesWithSession))
                .body(BodyInserters.fromFormData(formData))
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    log.debug("Full html page: {}", html);
                    assertThat(html)
                            .contains("<title>Twitter Search Engine</title>")
                            .contains("<a class=\"nav-link\" href=\"/elastic-query-web-client/\">Main page</a>")
                            .contains("<a class=\"nav-link\" href=\"/elastic-query-web-client/home\">Search page</a>")
                            .contains("<h1>Query Client</h1>")
                            .contains("<th scope=\"row\">" + expectedResponseModel.getId() + "</th>")
                            .contains("<td>" + expectedResponseModel.getUserId() + "</td>")
                            .contains("<td>" + expectedResponseModel.getText() + "</td>")
                    ;
                });
        then(elasticWebClient).should().getDataByText(eq(expectedRequestModel));
    }


}