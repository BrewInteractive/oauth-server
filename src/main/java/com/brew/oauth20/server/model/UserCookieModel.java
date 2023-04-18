package com.brew.oauth20.server.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record UserCookieModel(Long userId, OffsetDateTime expiresAt) {
    public static UserCookieModel parse(String value) throws IllegalArgumentException {
        var splitValues = value.split(":");
        if (splitValues.length < 2) {
            throw new IllegalArgumentException("Invalid value format: " + value);
        }
        return new UserCookieModel(Long.parseLong(splitValues[0]), OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(splitValues[1])), ZoneOffset.UTC));
    }
}
