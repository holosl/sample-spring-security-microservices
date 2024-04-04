package pl.piomin.samples.security.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** This service mocks the callme service
 * */
@RestController
public class CallmeControllerMock {
    public final static String pingPath = "/callme/ping";
    public final static String pingResponse = "Hello!";

    @GetMapping(pingPath)
    public String ping() {
        return pingResponse;
    }
}
