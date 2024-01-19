package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.TokenModel;

import java.util.Map;

public interface TokenService {
    TokenModel generateToken(ClientModel client, String state, Map<String, Object> additionalClaims);

    TokenModel generateToken(ClientModel client, String userId, String state, Map<String, Object> additionalClaims, String refreshToken);


}

