package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.service.JwtService;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {

    private static final int REFRESH_TOKEN_LENGTH = 64;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public TokenModel generateToken(ClientModel client, String userId, String state) {
        if (Boolean.TRUE.equals(client.issueRefreshTokens())) {
            var refreshToken = refreshTokenService.createRefreshToken(client.clientId(), userId, StringUtils.generateSecureRandomString(REFRESH_TOKEN_LENGTH), client.refreshTokenExpiresInDays());
            return generateToken(client, userId, state, refreshToken.getToken());
        }

        var signTokenOptions = createSignTokenOptions(client, userId, state);
        return jwtService.signToken(signTokenOptions);
    }

    @Override
    public TokenModel generateToken(ClientModel client, String userId, String state, String refreshToken) {
        var signTokenOptions = createSignTokenOptions(client, userId, state);
        return jwtService.signToken(signTokenOptions, refreshToken);
    }

    @Override
    public TokenModel generateToken(ClientModel client, String state) {
        var signTokenOptions = createSignTokenOptions(client, null, state);
        return jwtService.signToken(signTokenOptions);
    }

    private Map<String, Object> createAdditionalClaims(ClientModel client) {
        return Map.of("clientId", client.clientId());
    }

    private SignTokenOptions createSignTokenOptions(ClientModel client, String userId, String state) {
        return new SignTokenOptions(userId == null ? null : userId, client.audience(), client.issuerUri(), state, client.tokenExpiresInMinutes(), client.clientSecretDecoded(), createAdditionalClaims(client));
    }
}D
