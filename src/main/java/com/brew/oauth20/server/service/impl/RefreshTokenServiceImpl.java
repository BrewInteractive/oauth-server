package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.exception.RefreshTokenNotFoundException;
import com.brew.oauth20.server.mapper.RefreshTokenMapper;
import com.brew.oauth20.server.repository.ActiveRefreshTokenRepository;
import com.brew.oauth20.server.repository.RefreshTokenRepository;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final int REFRESH_TOKEN_LENGTH = 64;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ActiveRefreshTokenRepository activeRefreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   ActiveRefreshTokenRepository activeRefreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.activeRefreshTokenRepository = activeRefreshTokenRepository;
    }

    @Override
    public RefreshToken createRefreshToken(ClientUser clientUser, int expirationTimeInDays) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiresAt = now.plusDays(expirationTimeInDays);

        String token = StringUtils.generateSecureRandomString(REFRESH_TOKEN_LENGTH);

        RefreshToken refreshToken = RefreshToken.builder()
                .clientUser(clientUser)
                .token(token)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Override
    public RefreshToken revokeRefreshToken(String clientId, String token, int expirationTimeInDays) {
        var activeRefreshToken = activeRefreshTokenRepository.findByToken(token);

        if (activeRefreshToken.isEmpty())
            throw new RefreshTokenNotFoundException(token);

        var existingRefreshToken = RefreshTokenMapper.INSTANCE.toRefreshToken(activeRefreshToken.get());

        var newRefreshToken = createRefreshToken(activeRefreshToken.get().getClientUser(), expirationTimeInDays);

        existingRefreshToken.setReplacedByToken(newRefreshToken);
        existingRefreshToken.setRevokedAt(OffsetDateTime.now());

        refreshTokenRepository.save(existingRefreshToken);

        return newRefreshToken;
    }
}

