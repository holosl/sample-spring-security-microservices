package pl.piomin.samples.security.callme;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
}
