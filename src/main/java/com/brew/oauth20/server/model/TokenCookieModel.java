package com.brew.oauth20.server.model;

import lombok.Builder;

@Builder
public record TokenCookieModel(
        String accessToken,
        String refreshToken,
        int refreshTokenExpiresInDays,
        int accessTokenExpiresInMinutes
) {
}