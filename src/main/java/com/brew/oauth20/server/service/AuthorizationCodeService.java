package com.brew.oauth20.server.service;

public interface AuthorizationCodeService {
    String createAuthorizationCode(Long userId, String redirectUri, long expiresIn, String clientId) throws Exception;

    String getAuthorizationCode(String code, String redirectUri, boolean markAsUsed);
}
