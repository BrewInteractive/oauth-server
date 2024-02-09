package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;

import java.util.Map;

public interface TokenService {
    String generateToken(ClientModel client, String state, Map<String, Object> additionalClaims);

    String generateToken(ClientModel client, String userId, String state, String scope, Map<String, Object> additionalClaims);

}

