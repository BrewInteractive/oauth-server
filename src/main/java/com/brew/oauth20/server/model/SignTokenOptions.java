package com.brew.oauth20.server.model;

import java.util.Map;

public record SignTokenOptions(
        String subject,
        String audience,
        String issuerUri,
        Integer expiresInSeconds,
        Map<String, Object> additionalClaims
) {
}