package pl.piomin.samples.security.callme;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import pl.piomin.samples.security.common.Scope;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CallMeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenNoTokenThenUnautorized() throws Exception {
        this.mockMvc.perform(get("/callme/pong")).andExpect(status().isUnauthorized());
    }
    @Test
    void whenNoAuthorityScopeRequiredThenAuthenticationIsEnough() throws Exception {
        this.mockMvc.perform(get("/callme/pong").with(jwt())).andExpect(status().isOk());
    }
    @Test
    void whenLackingScopeThenUnauthorized() throws Exception {
        this.mockMvc.perform(get("/callme/ping").with(jwt())).andExpect(status().isForbidden());
    }

    @Test
    void whenHavingScopeThenOk() throws Exception {
        Jwt tokenWithClaim = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user")
                .claim("scope", "read")
                .claim("scope", Scope.c9)
                .build();
        this.mockMvc.perform(get("/callme/ping").with(jwt().authorities(List.of(new SimpleGrantedAuthority(Scope.c9))))).andExpect(status().isOk());
    }
}
