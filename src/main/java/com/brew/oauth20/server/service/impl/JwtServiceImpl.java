package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String signToken(SignTokenOptions signTokenOptions) {


        // create claims for JWT token
        Claims claims = Jwts.claims().
                setAudience(signTokenOptions.audience()).
                setIssuer(signTokenOptions.issuerUri()).
                setIssuedAt(Date.from(Instant.now())).
                setExpiration(Date.from(Instant.now().plusSeconds(signTokenOptions.expiresInSeconds())));

        if (signTokenOptions.subject() != null) {
            claims.setSubject(signTokenOptions.subject());
        }

        if (signTokenOptions.additionalClaims() != null) {
            claims.putAll(signTokenOptions.additionalClaims());
        }

        // sign JWT token
        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSigningKey(signTokenOptions.signingKey()))
                .compact();

    }

    private Key getSigningKey(String signingKey) {
        byte[] keyBytes = signingKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}

