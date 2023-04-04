package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
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
    private final ClientsUserRepository clientsUserRepository;


    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, ClientsUserRepository clientsUserRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.clientsUserRepository = clientsUserRepository;
    }


    @Override
    public void createRefreshToken(String clientId, Long userId, String token, int expirationTimeInDays) throws ClientsUserNotFoundException {

        Optional<ClientUser> clientsUser = clientsUserRepository.findByClientIdAndUserId(clientId, userId);

        if (clientsUser.isEmpty())
            throw new ClientsUserNotFoundException(clientId, userId);

        OffsetDateTime currentDate = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expirationDate = currentDate.plusDays(expirationTimeInDays);

        RefreshToken refreshToken = RefreshToken.builder()
                .clientUser(clientsUser.get())
                .token(token)
                .expiresAt(expirationDate)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}

