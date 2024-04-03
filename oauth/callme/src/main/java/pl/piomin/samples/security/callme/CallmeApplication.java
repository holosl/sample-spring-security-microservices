package pl.piomin.samples.security.callme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pl.piomin.samples.security.common.SecurityConfig;

@SpringBootApplication
@Import({SecurityConfig.class})
public class CallmeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallmeApplication.class, args);
    }

}
