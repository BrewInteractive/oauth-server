package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.exception.UserIdentityServiceException;
import com.brew.oauth20.server.http.RestTemplateWrapper;
import com.brew.oauth20.server.model.UserIdentityInfoModel;
import com.brew.oauth20.server.service.UserIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

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
    public UserIdentityInfoModel getUserIdentity(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", accessToken);
            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<UserIdentityInfoModel> responseEntity = restTemplate.exchange(userIdentityServiceUrl, HttpMethod.GET, requestEntity, UserIdentityInfoModel.class);
            return responseEntity.getBody();
        } catch (HttpServerErrorException e) {
            throw new UserIdentityServiceException(e);
        }
    }
}
