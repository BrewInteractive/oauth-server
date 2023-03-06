package com.brew.oauth20.server.service;

public interface AuthorizationCodeService {
    String createAuthorizationCode(String subject,String redirectUri,long expiresIn,int clientId);
    String getAuthorizationCode(String code,String redirectUri,boolean markAsUsed);
}
