package com.brew.oauth20.server.service.impl;


import com.brew.oauth20.server.service.AuthorizationCodeService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {
    @Override
    public String createAuthorizationCode(String subject, String redirectUri, long expiresIn, UUID clientId) {
        return null;
    }

    @Override
    public String getAuthorizationCode(String code, String redirectUri, boolean markAsUsed) {
        return null;
    }
}
