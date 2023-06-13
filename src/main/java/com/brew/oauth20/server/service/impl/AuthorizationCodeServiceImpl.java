package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.AuthorizationCode;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.repository.ActiveAuthorizationCodeRepository;
import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import com.brew.oauth20.server.repository.ClientsUserRepository;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository;
    private final ClientsUserRepository clientsUserRepository;

    public AuthorizationCodeServiceImpl(AuthorizationCodeRepository authorizationCodeRepository,
                                        ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository,
                                        ClientsUserRepository clientsUserRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.activeAuthorizationCodeRepository = activeAuthorizationCodeRepository;
        this.clientsUserRepository = clientsUserRepository;
    }

    @Override
    public String createAuthorizationCode(Long userId, String redirectUri, long expiresIn, String clientId) {
        var optionalClientUser = clientsUserRepository.findByClientIdAndUserId(clientId, userId);
        if (optionalClientUser.isEmpty())
            throw new ClientNotFoundException(clientId);
        var clientUser = optionalClientUser.get();
        OffsetDateTime expiresAt = OffsetDateTime.now().plus(Duration.ofMillis(expiresIn));
        String code = StringUtils.generateSecureRandomString();
        var authorizationCode = AuthorizationCode.builder()
                .clientUser(clientUser)
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
        if (activeAuthorizationCode.isEmpty()) {
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
