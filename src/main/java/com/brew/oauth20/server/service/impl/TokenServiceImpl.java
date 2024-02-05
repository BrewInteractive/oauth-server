package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.service.JwtService;
import com.brew.oauth20.server.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;

    @Autowired
    public TokenServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(ClientModel client, String state, Map<String, Object> additionalClaims) {
        return generateToken(client, null, state, null, additionalClaims);
    }

    @Override
    public String generateToken(ClientModel client, String userId, String state, String scope, Map<String, Object> additionalClaims) {
        var signTokenOptions = createSignTokenOptions(client, userId, scope, additionalClaims);
        return jwtService.signToken(signTokenOptions);
    }

    private SignTokenOptions createSignTokenOptions(ClientModel client, String userId, String scope, Map<String, Object> additionalClaims) {
        return new SignTokenOptions(
                userId,
                client.clientId(),
                scope,
                client.audience(),
                client.issuerUri(),
                client.tokenExpiresInMinutes() * 60,
                client.clientSecretDecoded(),
                additionalClaims
        );
    }
}
