package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.service.JwtService;
import com.brew.oauth20.server.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private final JwtService jwtService;

    @Autowired
    public TokenServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public TokenModel generateToken(ClientModel client, String userId, String state, Map<String, Object> additionalClaims, String refreshToken) {
        var signTokenOptions = createSignTokenOptions(client, userId, additionalClaims);
        var accessToken = jwtService.signToken(signTokenOptions);

        return buildToken(accessToken, refreshToken, state, signTokenOptions.expiresInSeconds());
    }

    @Override
    public TokenModel generateToken(ClientModel client, String state, Map<String, Object> additionalClaims) {
        var signTokenOptions = createSignTokenOptions(client, null, additionalClaims);
        var accessToken = jwtService.signToken(signTokenOptions);
        return buildToken(accessToken, null, state, signTokenOptions.expiresInSeconds());
    }


    private SignTokenOptions createSignTokenOptions(ClientModel client, String userId, Map<String, Object> additionalClaims) {
        return new SignTokenOptions(
                userId,
                client.audience(),
                client.issuerUri(),
                client.tokenExpiresInMinutes() * 60,
                client.clientSecretDecoded(),
                additionalClaims
        );
    }

    private TokenModel buildToken(String accessToken, String refreshToken, String state, long expiresIn) {
        var tokenModelBuilder = TokenModel.builder()
                .accessToken(accessToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(expiresIn)
                .state(state);
        if (refreshToken != null)
            tokenModelBuilder.refreshToken(refreshToken);
        return tokenModelBuilder.build();
    }
}
