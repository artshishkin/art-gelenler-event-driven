package net.shyshkin.study.microservices.elasticquerywebclient;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.DefaultJavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.Cookie;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import net.shyshkin.study.microservices.config.UserConfigData;
import net.shyshkin.study.microservices.elasticquerywebclient.service.ElasticWebClient;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientResponseModel;
import net.shyshkin.study.microservices.util.VersionUtil;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "server.port=8094"
})
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ContextConfiguration(initializers = ElasticQueryWebClientApplicationTests.Initializer.class)
class ElasticQueryWebClientApplicationTests {

    private static final String REALM_NAME = "gelenler-tutorial";

    private static final String APP_USER_USERNAME = "app.user";
    private static final String APP_USER_PASSWORD = "123";
    private static final String APP_ADMIN_USERNAME = "app.admin";
    private static final String APP_ADMIN_PASSWORD = "234";
    private static final String APP_SUPER_USER_USERNAME = "app.superuser";
    private static final String APP_SUPER_USER_PASSWORD = "345";

    private static final String REALM_FILE_PATH = "../docker-compose/export/gelenler-tutorial-realm.json";
    private static final String DEFAULT_REALM_IMPORT_FILES_LOCATION = "/opt/keycloak/data/import/";

    private String baseUri;

    @LocalServerPort
    int serverPort;

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Autowired
    Environment environment;

    @Autowired
    ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserConfigData userConfigData;

    @MockBean
    ElasticWebClient elasticWebClient;

    static String csrfValue;
    static LinkedMultiValueMap<String, String> cookiesWithSession;

    com.gargoylesoftware.htmlunit.WebClient htmlUnitWebClient;

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:" + VersionUtil.getVersion("KEYCLOAK_VERSION"))
            .withAdminUsername("admin")
            .withAdminPassword("Pa55w0rd")
            .withRealmImportFile(".") //fake insert to enable flag --import realm
            .withCopyFileToContainer(MountableFile.forHostPath(REALM_FILE_PATH), DEFAULT_REALM_IMPORT_FILES_LOCATION + FilenameUtils.getName(REALM_FILE_PATH))
            .withStartupTimeout(Duration.ofMinutes(4));

    @BeforeEach
    void setUp() {

        System.setProperty("app.redirect.host.uri", "http://localhost:" + serverPort);
        System.setProperty("app.security.logout-success-uri", "http://localhost:" + serverPort + "/elastic-query-web-client");

        this.htmlUnitWebClient = new WebClient();
        this.baseUri = "http://localhost:" + serverPort + "/elastic-query-web-client";
        this.htmlUnitWebClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        this.htmlUnitWebClient.getOptions().setThrowExceptionOnScriptError(false);
        this.htmlUnitWebClient.getOptions().setRedirectEnabled(true);
        this.htmlUnitWebClient.setAjaxController(new NicelyResynchronizingAjaxController());
        this.htmlUnitWebClient.setJavaScriptErrorListener(new DefaultJavaScriptErrorListener() {
            @Override
            public void scriptException(HtmlPage page, ScriptException scriptException) {
                log.debug("JS error in line {} column {} Failing line\n`{}`\nscript source code {}",
                        scriptException.getFailingLineNumber(),
                        scriptException.getFailingColumnNumber(),
                        scriptException.getFailingLine(),
                        scriptException.getScriptSourceCode()
                );
                scriptException.printStackTrace();
            }
        });
        this.htmlUnitWebClient.setCssErrorHandler(new SilentCssErrorHandler()); //skip CSS warnings and errors
        this.htmlUnitWebClient.getCookieManager().clearCookies();    // log out
    }

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
    @DisplayName("When unauthorized user tries to access /query-by-text endpoint he should be redirected to oath2/authorization endpoint")
    void unauthorized() {

        //when
        webTestClient.get().uri("/query-by-text")
                .exchange()

                //then
                .expectStatus().is3xxRedirection()
                .expectHeader().location("http://localhost:" + serverPort + "/elastic-query-web-client/oauth2/authorization/keycloak");
    }

    @Test
    @Order(25)
    @DisplayName("When unauthorized user tries to access /query-by-text endpoint finally he should see login page")
    void unauthorized_usingHtmlUnitWebClient() throws IOException {

        //given
        String queryByTextPageUrl = baseUri + "/query-by-text";

        //when
        HtmlPage page = htmlUnitWebClient.getPage(queryByTextPageUrl);

        //then
        assertLoginPage(page);
    }

    @Test
    @Order(30)
    @DisplayName("Access to index page is always allowed")
    void index() {

        //when
        webTestClient.get().uri("/")
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    log.debug("Full html page: {}", html);
                    assertThat(html)
                            .contains("<title>Twitter Search Engine</title>")
                            .contains("<a class=\"nav-link\" href=\"/elastic-query-web-client/\">Main page</a>")
                            .doesNotContain("<a class=\"nav-link\" href=\"/elastic-query-web-client/home\">Search page</a>")
                            .contains("<form class=\"col-12\" method=\"get\" action=\"/elastic-query-web-client/home\">")
                            .contains("<button id=\"login-button\" class=\"btn btn-primary float-right\" type=\"submit\">Login</button>")
                            .contains("<p>Please <a href=\"/elastic-query-web-client/home\">login</a> to start searching</p>")
                    ;
                });
    }

    @Test
    @Order(40)
    @DisplayName("When accessing Home page and providing correct credentials then should show Home page")
    void searchPage() throws IOException {

        //given
        String queryByTextPageUrl = baseUri + "/home";

        //when
        HtmlPage page = htmlUnitWebClient.getPage(queryByTextPageUrl);
        assertLoginPage(page);
        page = signIn(page, APP_SUPER_USER_USERNAME, APP_SUPER_USER_PASSWORD);

        //then
        logPageContent(page);
        assertHomePage(page);

        HtmlInput csrfHiddenInput = page.querySelector("input[name=\"_csrf\"]");

        csrfValue = csrfHiddenInput.getValueAttribute();
        log.debug("CSRF: {}", csrfValue);

        Set<Cookie> cookies = htmlUnitWebClient.getCookieManager().getCookies();
        log.debug("Cookies: {}", cookies);
        Cookie jsessionid = htmlUnitWebClient.getCookieManager().getCookie("JSESSIONID");
        log.debug("JSESSIONID: {}", jsessionid);
        cookiesWithSession = new LinkedMultiValueMap<>();
        cookiesWithSession.add("JSESSIONID", jsessionid.getValue());
    }

    @Test
    @Order(50)
    @DisplayName("When posting query to /query-by-text endpoint with correct CSRF code in form input value and JSESSIONID Cookie then should query backend service by text")
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

        webTestClient.post().uri("/query-by-text")
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

    @Test
    @Order(60)
    @DisplayName("When login with correct credentials, accessing Home page, searching by text then should query backend service to search by text")
    void fullWorkflowTest() throws IOException {

        //given
        String queryByTextPageUrl = baseUri + "/home";
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
        HtmlPage page = htmlUnitWebClient.getPage(queryByTextPageUrl);
        assertLoginPage(page);
        page = signIn(page, APP_SUPER_USER_USERNAME, APP_SUPER_USER_PASSWORD);
        assertHomePage(page);

        HtmlInput searchTextInput = page.querySelector("input[id=\"text\"]");
        searchTextInput.type(searchText);
        HtmlInput searchButton = page.querySelector("input[value=\"Search\"]");
        page = searchButton.click();

        //then
        then(elasticWebClient).should().getDataByText(eq(expectedRequestModel));
        logPageContent(page);

        HtmlTable resultTable = page.querySelector("table");
        HtmlTableCell searchResultText = resultTable.getCellAt(1, 2);

        assertThat(searchResultText.getTextContent()).isEqualTo(expectedResponseModel.getText());
    }

    @Test
    @Order(70)
    @DisplayName("When login and logout then should redirect to login page when trying to access home page")
    void logoutTest() throws IOException {

        //given
        String queryByTextPageUrl = baseUri + "/home";
        HtmlPage page = htmlUnitWebClient.getPage(queryByTextPageUrl);
        assertLoginPage(page);
        page = signIn(page, APP_SUPER_USER_USERNAME, APP_SUPER_USER_PASSWORD);
        assertHomePage(page);

        //when
        logPageContent(page);
        page = logout(page);

        //then
        assertIndexPage(page);
        page = htmlUnitWebClient.getPage(queryByTextPageUrl);
        assertLoginPage(page);
    }

    private void logPageContent(HtmlPage page) {
        log.debug("page content:\n{}", page.getWebResponse().getContentAsString());
    }

    private static <P extends Page> P signIn(HtmlPage page, String username, String password) throws IOException {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlInput signInButton = page.querySelector("input[name=\"login\"]");

        usernameInput.type(username);
        passwordInput.type(password);
        return signInButton.click();
    }

    private <P extends Page> P logout(HtmlPage page) throws IOException {
        HtmlButton logoutButton = page.querySelector("button[id=\"logout-button\"]");
        return logoutButton.click();
    }

    private static void assertLoginPage(HtmlPage page) {
        assertThat(page.getUrl().toString()).contains("/realms/" + REALM_NAME + "/protocol/openid-connect/auth");

        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlInput signInButton = page.querySelector("input[name=\"login\"]");
        String title = page.getTitleText();

        assertThat(title).isEqualTo("Sign in to " + REALM_NAME);
        assertThat(usernameInput).isNotNull();
        assertThat(passwordInput).isNotNull();
        assertThat(signInButton.getValueAttribute()).isEqualToIgnoringCase("Sign In");
    }

    private static void assertIndexPage(HtmlPage page) {

        assertThat(page.getUrl().toString()).endsWith("/elastic-query-web-client/");
        assertThat(page.getWebResponse().getContentAsString())
                .contains("<p>Please <a href=\"/elastic-query-web-client/home\">login</a> to start searching</p>");

        String title = page.getTitleText();
        assertThat(title).isEqualTo("Twitter Search Engine");

        var loginButton = page.getElementById("login-button");
        assertThat(loginButton)
                .isNotNull()
                .satisfies(button -> assertThat(button.getTextContent()).isEqualTo("Login"));
    }

    private static void assertHomePage(HtmlPage page) {
        assertThat(page.getUrl().toString()).contains("/home");

        HtmlAnchor mainPageAnchor = page.querySelector("a[href=\"/elastic-query-web-client/\"]");
        HtmlAnchor homePageAnchor = page.querySelector("a[href=\"/elastic-query-web-client/home\"]");
        HtmlInput csrfHiddenInput = page.querySelector("input[name=\"_csrf\"]");
        var logoutButton = page.getElementById("logout-button");

        String title = page.getTitleText();

        assertThat(title).isEqualTo("Twitter Search Engine");
        assertThat(mainPageAnchor).isNotNull();
        assertThat(homePageAnchor).isNotNull();
        assertThat(UUID.fromString(csrfHiddenInput.getValueAttribute()))
                .isInstanceOf(UUID.class);
        assertThat(logoutButton)
                .isNotNull()
                .satisfies(button -> assertThat(button.getTextContent()).isEqualTo("Logout"));
    }

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            String issuerUriProperty = String.format(
                    "app.oauth.baseUri=%s",
                    keycloakContainer.getAuthServerUrl()
                            .trim()
                            .replaceFirst("/$", "")
            );
            log.debug("issuerUriProperty: {}", issuerUriProperty);

            TestPropertyValues
                    .of(issuerUriProperty)
                    .applyTo(applicationContext.getEnvironment());
        }
    }

}