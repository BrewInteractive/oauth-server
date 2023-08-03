package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret.key}")
    String jwtSecretKey;
    @Override
    public TokenModel signToken(SignTokenOptions signTokenOptions) {
        var expiresInSeconds = signTokenOptions.tokenExpiresInMinutes() * 60L;

        // create claims for JWT token
        Claims claims = Jwts.claims().
                setAudience(signTokenOptions.audience()).
                setIssuer(signTokenOptions.issuerUri()).
                setIssuedAt(Date.from(Instant.now())).
                setExpiration(Date.from(Instant.now().plusSeconds(expiresInSeconds)));

        if (signTokenOptions.subject() != null) {
            claims.setSubject(signTokenOptions.subject());
        }

        if (signTokenOptions.additionalClaims() != null) {
            claims.putAll(signTokenOptions.additionalClaims());
        }

        // sign JWT token
        var token = Jwts.builder()
                .setClaims(claims)
                .signWith(getSigningKey(jwtSecretKey))
                .compact();

        // create token model object and return
        return TokenModel.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresInSeconds)
                .state(signTokenOptions.state())
                .build();
    }

    @Override
    public TokenModel signToken(SignTokenOptions signTokenOptions, String refreshToken) {
        var token = signToken(signTokenOptions);
        token.setRefreshToken(refreshToken);
        return token;
    }

    private Key getSigningKey(String signingKey) {
        byte[] keyBytes = signingKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}

