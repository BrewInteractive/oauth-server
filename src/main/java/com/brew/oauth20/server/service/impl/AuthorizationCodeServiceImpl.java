package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.AuthorizationCode;
import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

    private final AuthorizationCodeRepository authorizationCodeRepository;

    public AuthorizationCodeServiceImpl(AuthorizationCodeRepository authorizationCodeRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository;
    }

    @Override
    public String createAuthorizationCode(Long userId, String redirectUri, long expiresIn, UUID clientId) {
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(expiresIn), ZoneOffset.UTC);
        String code = StringUtils.generateSecureRandomString();
        var authorizationCode = new AuthorizationCode();
        authorizationCode.setClientId(clientId);
        authorizationCode.setUserId(userId);
        authorizationCode.setCode(code);
        authorizationCode.setRedirectUri(redirectUri);
        authorizationCode.setExpiresAt(expiresAt);
        authorizationCodeRepository.save(authorizationCode);
        return code;
    }

    @Override
    public String getAuthorizationCode(String code, String redirectUri, boolean markAsUsed) {
        var authorizationCode = authorizationCodeRepository.findByCodeAndRedirectUri(code, redirectUri);
        if (markAsUsed) {
            authorizationCode.setUsedAt(OffsetDateTime.now());
            authorizationCodeRepository.save(authorizationCode);
        }
        return authorizationCode.getCode();
    }
}
