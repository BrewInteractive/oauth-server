package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.exception.RefreshTokenNotFoundException;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String clientId, String userId, int expirationTimeInDays) throws ClientsUserNotFoundException;

    RefreshToken revokeRefreshToken(String clientId, String token, int expirationTimeInDays) throws RefreshTokenNotFoundException;
}

