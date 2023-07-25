package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.TokenModel;

public interface TokenService {
    TokenModel generateToken(ClientModel client, String state);

    TokenModel generateToken(ClientModel client, String userId, String state);

    TokenModel generateToken(ClientModel client, String userId, String state, String refreshToken);
}

