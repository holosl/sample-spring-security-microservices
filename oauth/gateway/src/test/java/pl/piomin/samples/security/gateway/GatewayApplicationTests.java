package pl.piomin.samples.security.gateway;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewayApplicationTests {

    static String accessToken;

    static final String realmConfigPath = "realm-export.json";
    // the following values should be in the json indicated byrealmConfigPath
    static final String clientId = "c9-client";
    static final String username = "c9-holosl";
    static final String password = "pass";
    static final String realm = "c9realm";
    @Autowired
    WebTestClient webTestClient;

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile(realmConfigPath)
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> String.format("%s/realms/%s", keycloak.getAuthServerUrl(), realm));
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> String.format("%s/realms/%s/protocol/openid-connect/certs", keycloak.getAuthServerUrl(), realm));

        // set up routing so that the callme-service calls are forwarded to the mock controller
        registry.add("spring.cloud.gateway.routes[0].uri",
                () -> "http://localhost:8060");
        registry.add("spring.cloud.gateway.routes[0].id", () -> "callme-service");
        registry.add("spring.cloud.gateway.routes[0].predicates[0]", () -> "Path=/callme/**");
    }

    @Test
    @Order(1)
    void shouldStart() {

    }

    @Test
    @Order(1)
    void shouldBeRedirectedToLoginPage() {
        webTestClient.get().uri("/callme/ping")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @Order(2)
    void shouldObtainAccessToken() throws URISyntaxException {
        URI authorizationURI = new URIBuilder(
                String.format("%s/realms/%s/protocol/openid-connect/token",
                        keycloak.getAuthServerUrl(), realm)
        ).build();

        WebClient webclient = WebClient.builder().build();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList(clientId));
        formData.put("username", Collections.singletonList(username));
        formData.put("password", Collections.singletonList(password));

        String result = webclient.post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        accessToken = jsonParser.parseMap(result)
                .get("access_token")
                .toString();
        assertNotNull(accessToken);
    }

    @Test
    @Order(3)
    void shouldReturnToken() {
        webTestClient.get().uri(CallmeControllerMock.pingPath)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class).isEqualTo(CallmeControllerMock.pingResponse);
    }
}
