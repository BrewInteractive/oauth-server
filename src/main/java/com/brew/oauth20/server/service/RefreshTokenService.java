package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(ClientUser clientUser, int expirationTimeInDays);

    RefreshToken revokeRefreshToken(String clientId, String token, int expirationTimeInDays);
}

