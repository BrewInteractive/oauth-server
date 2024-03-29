package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.exception.UserIdentityServiceException;
import com.brew.oauth20.server.http.RestTemplateWrapper;
import com.brew.oauth20.server.service.UserIdentityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

@Service
public class UserIdentityServiceImpl implements UserIdentityService {
    private final RestTemplateWrapper restTemplate;

    @Value("${id_token.user_identity_service_url}")
    String userIdentityServiceUrl;

    @Autowired
    public UserIdentityServiceImpl(RestTemplateWrapper restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> getUserIdentityInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", accessToken);
            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(userIdentityServiceUrl, HttpMethod.GET, requestEntity, JsonNode.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(responseEntity.getBody(), Map.class);
        } catch (HttpServerErrorException e) {
            throw new UserIdentityServiceException(e);
        }
    }
}