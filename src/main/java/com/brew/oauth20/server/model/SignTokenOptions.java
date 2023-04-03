package com.brew.oauth20.server.model;

import java.util.Map;

public record SignTokenOptions(
        String subject,
        String audience,
        String issuerUri,
        String state,
        Integer tokenExpiresInMinutes,
        String signingKey,
        Map<String, Object> additionalClaims
) {
}