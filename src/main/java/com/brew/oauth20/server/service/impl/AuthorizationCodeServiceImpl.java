package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.AuthorizationCode;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.repository.ActiveAuthorizationCodeRepository;
import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository;
    private final ClientRepository clientRepository;

    public AuthorizationCodeServiceImpl(AuthorizationCodeRepository authorizationCodeRepository,
                                        ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository,
                                        ClientRepository clientRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.activeAuthorizationCodeRepository = activeAuthorizationCodeRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public String createAuthorizationCode(Long userId, String redirectUri, long expiresIn, String clientId) {
        var optionalClient = clientRepository.findByClientId(clientId);
        if (optionalClient.isEmpty())
            throw new ClientNotFoundException(clientId);
        var client = optionalClient.get();
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(expiresIn), ZoneOffset.UTC);
        String code = StringUtils.generateSecureRandomString();
        var authorizationCode = AuthorizationCode.builder()
                .client(client)
                .userId(userId)
                .code(code)
                .redirectUri(redirectUri)
                .expiresAt(expiresAt)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        authorizationCodeRepository.save(authorizationCode);
        return code;
    }

    @Override
    public ActiveAuthorizationCode getAuthorizationCode(String code, String redirectUri, boolean markAsUsed) {
        var activeAuthorizationCode = activeAuthorizationCodeRepository.findByCodeAndRedirectUri(code, redirectUri);
        if(activeAuthorizationCode.isEmpty()){
            return null;
        }
        var activeAuthorizationCodeEntity = activeAuthorizationCode.get();
        if (markAsUsed) {
            var authorizationCode = AuthorizationCodeMapper.INSTANCE.toAuthorizationCode(activeAuthorizationCodeEntity);
            authorizationCode.setUsedAt(OffsetDateTime.now());
            authorizationCodeRepository.save(authorizationCode);
        }
        return activeAuthorizationCodeEntity;
    }
}
