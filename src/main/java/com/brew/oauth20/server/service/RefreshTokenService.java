package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.ClientsUserNotFoundException;

public interface RefreshTokenService {
    void createRefreshToken(String clientId, Long userId, String token, int expirationTimeInDays) throws ClientsUserNotFoundException;
}