package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.TokenModel;

import java.util.Map;

public interface JwtService {
    TokenModel signToken(String subject, String audience, String issuerUri, String state, Integer tokenExpiresInMinutes, String signingKey, Map<String, Object> additionalClaims);
}
