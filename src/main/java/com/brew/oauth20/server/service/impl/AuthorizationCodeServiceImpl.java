package com.brew.oauth20.server.service.impl;


import com.brew.oauth20.server.exception.AuthorizationCodeNotFoundException;
import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

    private final AuthorizationCodeRepository authorizationCodeRepository;

    public AuthorizationCodeServiceImpl(AuthorizationCodeRepository authorizationCodeRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository;
    }

    @Override
    public String createAuthorizationCode(String subject, String redirectUri, long expiresIn, UUID clientId) {
        return null;
    }

    @Override
    public String getAuthorizationCode(String code, String redirectUri, boolean markAsUsed) {
        return null;
    }

    @Override
    public void setAuthorizationCodeUsedAt(UUID id) {
        var authorizationCode = authorizationCodeRepository.findById(id);

        if (authorizationCode.isEmpty())
            throw new AuthorizationCodeNotFoundException("client not found");

        var entity = authorizationCode.get();

        entity.setUsedAt(OffsetDateTime.now());

        authorizationCodeRepository.save(entity);
    }
}
