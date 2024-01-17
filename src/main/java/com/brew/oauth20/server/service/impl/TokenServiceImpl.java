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
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    @Autowired
    public TokenServiceImpl(JwtService jwtService,
                            RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public TokenModel generateToken(ClientModel client, String userId, String state, Map<String, Object> additionalClaims) {
        if (Boolean.TRUE.equals(client.issueRefreshTokens())) {
            var refreshToken = refreshTokenService.createRefreshToken(client.clientId(), userId, StringUtils.generateSecureRandomString(REFRESH_TOKEN_LENGTH), client.refreshTokenExpiresInDays());
            return generateToken(client, userId, state, refreshToken.getToken(), client.refreshTokenExpiresInDays() * 24 * 60 * 60, additionalClaims);
        }

        var signTokenOptions = createSignTokenOptions(client, userId, additionalClaims);
        var accessToken = jwtService.signToken(signTokenOptions);
        return TokenModel.builder()
                .accessToken(accessToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(signTokenOptions.expiresInSeconds())
                .state(state)
                .build();
    }

    @Override
    public TokenModel generateToken(ClientModel client, String userId, String state, String refreshToken, int refreshTokenExpiresIn, Map<String, Object> additionalClaims) {
        var signTokenOptions = createSignTokenOptions(client, userId, additionalClaims);
        var accessToken = jwtService.signToken(signTokenOptions);
        var tokenModel = TokenModel.builder()
                .accessToken(accessToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(signTokenOptions.expiresInSeconds())
                .state(state)
                .build();
        tokenModel.setRefreshToken(refreshToken);
        tokenModel.setRefreshTokenExpiresIn(refreshTokenExpiresIn);
        return tokenModel;
    }

    @Override
    public TokenModel generateToken(ClientModel client, String state, Map<String, Object> additionalClaims) {
        var signTokenOptions = createSignTokenOptions(client, null, additionalClaims);
        var accessToken = jwtService.signToken(signTokenOptions);
        return TokenModel.builder()
                .accessToken(accessToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(signTokenOptions.expiresInSeconds())
                .state(state)
                .build();
    }

    private SignTokenOptions createSignTokenOptions(ClientModel client, String userId, Map<String, Object> additionalClaims) {
        return new SignTokenOptions(
                userId,
                client.audience(),
                client.issuerUri(),
                client.clientSecretDecoded(),
                client.tokenExpiresInMinutes() * 60,
                additionalClaims
        );
    }
}
