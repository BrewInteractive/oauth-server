package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.AuthorizationCode;

public interface AuthorizationCodeService {
    String createAuthorizationCode(Long userId, String redirectUri, long expiresIn, String clientId);

    AuthorizationCode getAuthorizationCode(String code, String redirectUri, boolean markAsUsed);
}
