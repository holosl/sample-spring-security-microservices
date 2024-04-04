package pl.piomin.samples.security.gateway;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** This service mocks the callme service
 * */
@RestController
@RequestMapping("/callme")
public class CallmeController {

    @GetMapping("/ping")
    public String ping() {
        return "Hello!";
    }
}
