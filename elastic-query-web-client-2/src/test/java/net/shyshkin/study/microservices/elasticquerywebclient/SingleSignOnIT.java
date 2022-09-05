package net.shyshkin.study.microservices.elasticquerywebclient;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.DefaultJavaScriptErrorListener;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.test.KeycloakAbstractTest;
import net.shyshkin.study.microservices.util.VersionUtil;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "server.port=8194"
})
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ContextConfiguration(initializers = SingleSignOnIT.Initializer.class)
class SingleSignOnIT extends KeycloakAbstractTest {

    private static final String REALM_NAME = "gelenler-tutorial";

    private static final String APP_USER_USERNAME = "app.user";
    private static final String APP_USER_PASSWORD = "123";
    private static final String APP_ADMIN_USERNAME = "app.admin";
    private static final String APP_ADMIN_PASSWORD = "234";
    private static final String APP_SUPER_USER_USERNAME = "app.superuser";
    private static final String APP_SUPER_USER_PASSWORD = "345";

    private String client1BaseUri;
    private String client2BaseUri;

    @LocalServerPort
    int serverPort;

    WebClient htmlUnitWebClient;

    private GenericContainer<?> elasticQueryWebClient_1 = new GenericContainer<>("artarkatesoft/art-gelenler-elastic-query-web-client:" + VersionUtil.getVersion("SERVICE_VERSION"));

    @BeforeEach
    void setUp() {

        System.setProperty("app.redirect.host.uri", "http://host.testcontainers.internal:" + serverPort);
        System.setProperty("app.security.logout-success-uri", "http://host.testcontainers.internal:" + serverPort + "/elastic-query-web-client");

        org.testcontainers.Testcontainers.exposeHostPorts(keycloakContainer.getHttpPort());

        if (!elasticQueryWebClient_1.isRunning()) {
            elasticQueryWebClient_1
                    .withAccessToHost(true)
                    .withEnv("SPRING_APPLICATION_JSON", "{\n" +
                            "  \"server\": {\n" +
                            "    \"servlet\": {\n" +
                            "      \"context-path\": \"/elastic-query-web-client\"\n" +
                            "    }\n" +
                            "  },\n" +
                            "  \"app.redirect.host.uri\": \"http://host.testcontainers.internal:" + serverPort + "\",\n" +
                            "  \"app.oauth.baseUri\": \"http://host.testcontainers.internal:" + keycloakContainer.getHttpPort() + "\",\n" +
                            "  \"app.security\": {\n" +
                            "    \"default-client-registration-id\": \"keycloak\",\n" +
                            "    \"logout-success-uri\": \"http://host.testcontainers.internal:" + serverPort + "/elastic-query-web-client\"\n" +
                            "  },\n" +
                            "  \"spring\": {\n" +
                            "    \"cloud\": {\n" +
                            "      \"config\": {\n" +
                            "        \"enabled\": false\n" +
                            "      }\n" +
                            "    },\n" +
                            "    \"thymeleaf\": {\n" +
                            "      \"cache\": false\n" +
                            "    },\n" +
                            "    \"security\": {\n" +
                            "      \"oauth2\": {\n" +
                            "        \"client\": {\n" +
                            "          \"registration\": {\n" +
                            "            \"keycloak\": {\n" +
                            "              \"client-id\": \"elastic-query-web-client\",\n" +
                            "              \"client-secret\": \"hKeXincDbrZvb9rnoJgAAqN8YsWNQPR2\",\n" +
                            "              \"authorization-grant-type\": \"authorization_code\",\n" +
                            "              \"scope\": \"openid,profile\",\n" +
                            "              \"redirect-uri\": \"{baseUrl}/login/oauth2/code/{registrationId}\"\n" +
                            "            }\n" +
                            "          },\n" +
                            "          \"provider\": {\n" +
                            "            \"keycloak\": {\n" +
                            "              \"issuer-uri\": \"${app.oauth.baseUri}/realms/gelenler-tutorial\"\n" +
                            "            }\n" +
                            "          }\n" +
                            "        }\n" +
                            "      }\n" +
                            "    }\n" +
                            "  },\n" +
                            "  \"elastic-query-web-client\": {\n" +
                            "    \"webclient\": {\n" +
                            "      \"connect-timeout-ms\": 10000,\n" +
                            "      \"read-timeout-ms\": 10000,\n" +
                            "      \"write-timeout-ms\": 10000,\n" +
                            "      \"max-in-memory-size\": 10485760,\n" +
                            "      \"content-type\": \"application/vnd.api.v1+json\",\n" +
                            "      \"accept-type\": \"application/vnd.api.v1+json\",\n" +
                            "      \"base-url\": \"http://elastic-query-service/elastic-query-service/documents\"\n" +
                            "    }\n" +
                            "  }\n" +
                            "}"
                    )
                    .withExposedPorts(8080)
                    .waitingFor(Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(3)))
                    .start();
        }

        this.client1BaseUri = "http://host.testcontainers.internal:" + elasticQueryWebClient_1.getMappedPort(8080) + "/elastic-query-web-client";
        this.client2BaseUri = "http://host.testcontainers.internal:" + serverPort + "/elastic-query-web-client";

        this.htmlUnitWebClient = new WebClient();
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
    @DisplayName("When login with correct credentials to CLIENT-1 then should be able to access home page on CLIENT-2 without login")
    void singleSignOnTest() throws IOException {

        //given
        HtmlPage page = htmlUnitWebClient.getPage(client1BaseUri + "/home");
        assertLoginPage(page);
        page = signIn(page, APP_SUPER_USER_USERNAME, APP_SUPER_USER_PASSWORD);
        assertHomePage(page);

        //when
        page = htmlUnitWebClient.getPage(client2BaseUri + "/home");

        //then
        assertHomePage(page);
    }

    @Test
    @Order(20)
    @DisplayName("When logout from CLIENT-1 then should be logged out from CLIENT-2")
    void logoutTest() throws IOException {

        //given
        HtmlPage page = htmlUnitWebClient.getPage(client2BaseUri + "/home");
        assertLoginPage(page);
        page = signIn(page, APP_SUPER_USER_USERNAME, APP_SUPER_USER_PASSWORD);
        assertHomePage(page);
        page = htmlUnitWebClient.getPage(client1BaseUri + "/home");
        assertHomePage(page);

        //when
        logPageContent(page);
        page = logout(page);

        //then
        assertIndexPage(page);
        page = htmlUnitWebClient.getPage(client2BaseUri + "/home");
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
                    "app.oauth.baseUri=%s", "http://host.testcontainers.internal:" + keycloakContainer.getHttpPort()
            );
            log.debug("issuerUriProperty: {}", issuerUriProperty);

            TestPropertyValues
                    .of(issuerUriProperty)
                    .applyTo(applicationContext.getEnvironment());
        }
    }

}