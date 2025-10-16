package com.example.StudentRbacApplication.service;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpaService {
    @Value("${app.opa-url}")
    private String opaUrl;

    private final RestTemplate rest = new RestTemplate();

    public boolean isAllowed(String username, String role, String action, Map<String, Object> resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> input = Map.of("user", Map.of("username", username, "role", role),
                                           "action", action,
                                           "resource", resource);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("input", input), headers);
        Map response = rest.postForObject(opaUrl, entity, Map.class);
        if(response == null) {
            return false;
        }
        Object result = response.get("result");
        if(result instanceof Map r){
            Object allow = r.get("allow");
            return Boolean.TRUE.equals(allow);
        }
        return false;
    }
}