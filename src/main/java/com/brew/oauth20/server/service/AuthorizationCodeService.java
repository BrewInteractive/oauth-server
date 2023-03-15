package com.brew.oauth20.server.service;

import java.util.UUID;

public interface AuthorizationCodeService {
    String createAuthorizationCode(Long userId, String redirectUri, long expiresIn, UUID clientId);

    String getAuthorizationCode(String code, String redirectUri, boolean markAsUsed);
}
