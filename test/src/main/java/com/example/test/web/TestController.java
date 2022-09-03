package com.example.test.web;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
        HttpResponse accessTokenResponse =
                HttpUtil.createPost("http://127.0.0.1:9000/oauth2/token")
                        .form("code", code)
                        .form("client_id", clientId)
                        .form("client_secret", "123456")
                        .form("grant_type", "authorization_code")
                        .form("redirect_uri", "http://127.0.0.1:8082/callback")
                        .execute();
        JSONObject jsonObject = JSONUtil.parseObj(accessTokenResponse.body());
        String accessToken = (String) jsonObject.get("access_token");
        HttpResponse httpResponse =
                HttpUtil.createGet("http://127.0.0.1:8090/messages")
                        .header("Authorization", "Bearer " + accessToken)
                        .execute();
        System.out.println(httpResponse.body());
    }
}
