package com.brew.oauth20.server.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static com.brew.oauth20.server.utils.StringUtils.parseCookieString;

public record UserCookieModel(
        String user_id,
        OffsetDateTime expires_at,
        String email,
        String countryCode,
        String phoneNumber
) {
    public static UserCookieModel parse(String cookieString) {
        Map<String, String> cookieMap = parseCookieString(cookieString);

        long epochSeconds = Long.parseLong(cookieMap.get("expires_at"));
        var expiresAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);

        return new UserCookieModel(
                cookieMap.get("user_id"),
                expiresAt,
                cookieMap.get("email"),
                cookieMap.get("country_code"),
                cookieMap.get("phone_number"));
    }
}
