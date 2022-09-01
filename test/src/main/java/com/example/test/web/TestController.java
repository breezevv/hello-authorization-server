package com.example.test.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class TestController {

    String clientId = "test";

    @GetMapping("/")
    public void getAuthorizationUrl(HttpServletResponse response) throws IOException {
        String location = String.format("http://localhost:9000/my/authorize?response_type=code&" +
                        "client_id=%s&scope=openid message.read message.write&" +
                        "redirect_uri=%s"
                , clientId, "http://127.0.0.1:8082/callback");
        response.sendRedirect(location);
    }

    @GetMapping("/callback")
    public void callback(String code) {
        System.out.println(code);
    }
}
