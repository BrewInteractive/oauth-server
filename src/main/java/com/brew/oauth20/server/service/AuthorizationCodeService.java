package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;

public interface AuthorizationCodeService {
    String createAuthorizationCode(Long userId, String redirectUri, long expiresIn, String clientId);

    ActiveAuthorizationCode getAuthorizationCode(String code, String redirectUri, boolean markAsUsed);
}
