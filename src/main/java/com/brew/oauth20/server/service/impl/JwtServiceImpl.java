package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtServiceImpl implements JwtService {
    @Override
    public TokenModel signToken(String subject, String audience, String issuerUri, String state, Integer tokenExpiresInMinutes, String signingKey, Map<String, Object> additionalClaims) {
        var expiresInSeconds = tokenExpiresInMinutes * 60L;

        // create claims for JWT token
        Claims claims = Jwts.claims().
                setSubject(subject).
                setAudience(audience).
                setIssuer(issuerUri).
                setIssuedAt(Date.from(Instant.now())).
                setExpiration(Date.from(Instant.now().plusSeconds(expiresInSeconds)));
        if (additionalClaims != null) {
            additionalClaims.forEach(claims::put);
        }

        // sign JWT token
        var token = Jwts.builder()
                .setClaims(claims)
                .signWith(getSigningKey(signingKey))
                .compact();

        // create token model object and return
        return TokenModel.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresInSeconds)
                .state(state)
                .build();
    }

    private Key getSigningKey(String signingKey) {
        byte[] keyBytes = signingKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}