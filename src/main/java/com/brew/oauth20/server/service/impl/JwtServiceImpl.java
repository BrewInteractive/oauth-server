package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtServiceImpl implements JwtService {
    @Override
    public TokenModel signToken(String subject, String audience, String issuerUri, String state, Integer tokenExpiresInMinutes, String signingKey, Map<String, Object> additionalClaims) {
        // create claims for JWT token
        Claims claims = Jwts.claims().
                setSubject(subject).
                setAudience(audience).
                setIssuer(issuerUri).
                setIssuedAt(Date.from(Instant.now())).
                setExpiration(Date.from(Instant.now().plusSeconds(tokenExpiresInMinutes * 60)));
        if (additionalClaims != null) {
            additionalClaims.forEach(claims::put);
        }

        // sign JWT token
        var token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();

        // create token model object and return
        return TokenModel.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tokenExpiresInMinutes * 60L)
                .state(state)
                .build();
    }
}