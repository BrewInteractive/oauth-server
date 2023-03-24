package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.service.JwtService;

import java.util.HashMap;

public class JwtServiceImpl implements JwtService {
    @Override
    public TokenModel signToken(String subject, String audience, String issuerUri, String state, Integer tokenExpiresInMinutes, String signingKey, HashMap<String, Object> additionalClaims) {
        return null;
    }
}
