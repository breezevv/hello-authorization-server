package com.example.authorization.server;

import com.example.authorization.server.entity.Client;
import com.example.authorization.server.repository.ClientRepository;
import com.example.authorization.server.repository.JpaRegisteredClientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JpaTest {

    @Resource
    private ClientRepository clientRepository;

    @Resource
    private JpaRegisteredClientRepository jpaRegisteredClientRepository;

    @Resource
    private ObjectMapper objectMapper ;

    @Test
    public void testSave() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "test");
        Client client = new Client();
        client.setId("2");
        client.setClientId("messaging-client");
        client.setClientIdIssuedAt(Instant.now());
        client.setClientSecret("{noop}secret");
        client.setClientName("messaging-client");
        client.setClientAuthenticationMethods("client_secret_basic");
        client.setAuthorizationGrantTypes("authorization_code");
        client.setRedirectUris("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc");
        client.setScopes("message.read,message.write");
        client.setClientSettings(objectMapper.writeValueAsString(map));
        client.setTokenSettings(objectMapper.writeValueAsString(map));

        clientRepository.save(client);
    }

    @Test
    public void testQuery() throws JsonProcessingException {
        Optional<Client> clientOptional = clientRepository.findByClientId("messaging-client");
        Client client = clientOptional.get();
        String data = client.getClientSettings();
//        Map<String, Object> map = new ObjectMapper().readValue(data, new TypeReference<Map<String, Object>>() {
//        });
        Map<String, Object> map = jpaRegisteredClientRepository.parseMap(data);
        System.out.println(map.toString());
//        System.out.println(jpaRegisteredClientRepository.findByClientId("messaging-client"));
    }

    private Map<String, Object> parseMap2(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}
