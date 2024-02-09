package com.brew.oauth20.server.model;

import java.util.Map;

public record SignTokenOptions(
        String subject,
        String authorizedParty,
        String scope,
        String audience,
        String issuerUri,
        Integer expiresInSeconds,
        String signingKey,
        Map<String, Object> additionalClaims
) {
}