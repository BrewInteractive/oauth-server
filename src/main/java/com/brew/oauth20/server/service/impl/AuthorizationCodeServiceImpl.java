package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.AuthorizationCode;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.repository.ActiveAuthorizationCodeRepository;
import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository;

    @Autowired
    public AuthorizationCodeServiceImpl(AuthorizationCodeRepository authorizationCodeRepository,
                                        ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.activeAuthorizationCodeRepository = activeAuthorizationCodeRepository;
    }

    @Override
    public String createAuthorizationCode(String redirectUri, long expiresIn, ClientUser clientUser) {
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
