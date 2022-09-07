package com.example.test.web;

import cn.hutool.http.ContentType;
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

    @GetMapping("register")
    public void register(HttpServletResponse response) throws IOException {
        String location = String.format("http://localhost:9000/my/authorize?response_type=code&" +
                        "client_id=%s&scope=client.create&" +
                        "redirect_uri=%s"
                , clientId, "http://127.0.0.1:8082/callback2");
        response.sendRedirect(location);
    }

    @GetMapping("/callback2")
    public void callback2(String code) {
        HttpResponse accessTokenResponse = HttpUtil.createPost("http://127.0.0.1:9000/oauth2/token")
                .form("code", code)
                .form("grant_type", "authorization_code")
                .form("client_id", clientId)
                .form("client_secret", "123456")
                .form("redirect_uri", "http://127.0.0.1:8082/callback2")
                .execute();
        JSONObject jsonObject = JSONUtil.parseObj(accessTokenResponse.body());
        String accessToken = (String) jsonObject.get("access_token");
        System.out.println(accessTokenResponse);
        JSONObject params = JSONUtil.createObj();
        // 参数看这个类 OidcClientMetadataClaimNames
        params.putOnce("client_name", "第三个注册的客户端");
        params.putOnce("scope", "message.read message.write");
        params.putOnce("grant_types", "authorization_code");
        params.putOnce("token_endpoint_auth_method", "client_secret_post");
        params.putOnce("redirect_uris", new String[] {"http:127.0.0.1:8082/callback3"});
        HttpResponse response = HttpUtil.createPost("http://127.0.0.1:9000/connect/register")
                .bearerAuth(accessToken)
                .contentType(ContentType.JSON.getValue())
                .body(params.toString()).execute();
        System.out.println(response);
    }
}
