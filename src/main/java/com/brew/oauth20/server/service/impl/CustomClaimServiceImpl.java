package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.exception.CustomClaimHookException;
import com.brew.oauth20.server.http.RestTemplateWrapper;
import com.brew.oauth20.server.model.CustomClaimsRequestModel;
import com.brew.oauth20.server.model.HookModel;
import com.brew.oauth20.server.service.CustomClaimService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

@Service
public class CustomClaimServiceImpl implements CustomClaimService {
    private final RestTemplateWrapper restTemplate;

    @Autowired
    public CustomClaimServiceImpl(RestTemplateWrapper restTemplate) {
        this.restTemplate = restTemplate;
    }

    @NotNull
    private static HttpEntity<CustomClaimsRequestModel> createRequest(HookModel customClaimHook, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        for (var hookHeader : customClaimHook.hookHeaderList())
            headers.add(hookHeader.key(), hookHeader.value());
        var requestBody = CustomClaimsRequestModel
                .builder()
                .userId(userId)
                .build();

        return new HttpEntity<>(requestBody, headers);
    }

    @Override
    public Map<String, Object> getCustomClaims(HookModel customClaimHook, String userId) {
        try {
            var requestEntity = createRequest(customClaimHook, userId);
            var responseEntity = restTemplate.exchange(customClaimHook.endpoint(), HttpMethod.POST, requestEntity, JsonNode.class);
            var objectMapper = new ObjectMapper();
            return objectMapper.convertValue(responseEntity.getBody(), Map.class);
        } catch (HttpServerErrorException e) {
            throw new CustomClaimHookException(e);
        }
    }
}
