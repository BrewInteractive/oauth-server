package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.exception.RefreshTokenNotFoundException;
import com.brew.oauth20.server.mapper.RefreshTokenMapper;
import com.brew.oauth20.server.repository.ActiveRefreshTokenRepository;
import com.brew.oauth20.server.repository.ClientsUserRepository;
import com.brew.oauth20.server.repository.RefreshTokenRepository;
import com.brew.oauth20.server.service.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final ActiveRefreshTokenRepository activeRefreshTokenRepository;
    private final ClientsUserRepository clientsUserRepository;


    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   ActiveRefreshTokenRepository activeRefreshTokenRepository,
                                   ClientsUserRepository clientsUserRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.activeRefreshTokenRepository = activeRefreshTokenRepository;
        this.clientsUserRepository = clientsUserRepository;
    }


    @Override
    public RefreshToken createRefreshToken(String clientId, Long userId, String token, int expirationTimeInDays) throws ClientsUserNotFoundException {

        Optional<ClientUser> clientsUser = clientsUserRepository.findByClientIdAndUserId(clientId, userId);

        if (clientsUser.isEmpty())
            throw new ClientsUserNotFoundException(clientId, userId);

        OffsetDateTime currentDate = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expirationDate = currentDate.plusDays(expirationTimeInDays);

        RefreshToken refreshToken = RefreshToken.builder()
                .clientUser(clientsUser.get())
                .token(token)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .expiresAt(expirationDate)
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Override
    public RefreshToken revokeRefreshToken(String clientId, String token, int expirationTimeInDays, String newToken) throws RefreshTokenNotFoundException {
        var activeRefreshToken = activeRefreshTokenRepository.findByToken(token);

        if (activeRefreshToken.isEmpty())
            throw new RefreshTokenNotFoundException(token);

        var existingRefreshToken = RefreshTokenMapper.INSTANCE.toRefreshToken(activeRefreshToken.get());

        var userId = activeRefreshToken.get().getClientUser().getUserId();

        var newRefreshToken = createRefreshToken(clientId, userId, newToken, expirationTimeInDays);

        existingRefreshToken.setReplacedByToken(newRefreshToken);
        existingRefreshToken.setRevokedAt(OffsetDateTime.now());

        refreshTokenRepository.save(existingRefreshToken);

        return newRefreshToken;
    }
}

