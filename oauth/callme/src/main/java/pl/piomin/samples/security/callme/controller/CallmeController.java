package pl.piomin.samples.security.callme.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piomin.samples.security.common.Scope;

@RestController
@RequestMapping("/callme")
public class CallmeController {

    @PreAuthorize("hasAuthority('" + Scope.c9 + "')")
    @GetMapping("/ping")
    public String ping() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return "Scopes: " + authentication.getAuthorities();
    }

    @GetMapping("/pong")
    public String pong() {
        return "unprotected";
    }
}
