package com.example.authorization.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ObjectMapperTest {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String data = "{\"name\":\"test\"}";
        objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
        });
    }
}
