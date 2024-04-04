package pl.piomin.samples.security.callme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import pl.piomin.samples.security.common.Scope;
import pl.piomin.samples.security.common.SecurityConfig;

@SpringBootApplication
@Import({SecurityConfig.class})
public class CallmeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallmeApplication.class, args);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/callme/pong").authenticated()
                        .anyRequest().hasAuthority(Scope.c9)

                )
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
