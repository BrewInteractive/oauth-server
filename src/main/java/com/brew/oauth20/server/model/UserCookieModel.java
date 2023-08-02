package com.brew.oauth20.server.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record UserCookieModel(
        String user_id,
        OffsetDateTime expires_at,
        String email,
        String countryCode,
        String phoneNumber
) {
    public static UserCookieModel parse(String cookieString) {
        try {
            if (cookieString == null)
                return null;
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(cookieString);
            String userId = jsonNode.get("user_id").asText();
            String email = jsonNode.get("email").asText();
            String countryCode = jsonNode.get("country_code").asText();
            String phoneNumber = jsonNode.get("phone_number").asText();
            long expiresAtEpoch = jsonNode.get("expires_at").asLong();
            OffsetDateTime expiresAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(expiresAtEpoch), ZoneOffset.UTC);
            return new UserCookieModel(userId, expiresAt, email, countryCode, phoneNumber);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new IllegalArgumentException();
        }
    }
    public static String toString(UserCookieModel model) {
        return "{"
                + "\"user_id\": \"" + model.user_id + "\","
                + "\"email\": \"" + model.email + "\","
                + "\"country_code\": \"" + model.countryCode + "\","
                + "\"phone_number\": \"" + model.phoneNumber + "\","
                + "\"expires_at\": " + model.expires_at.toEpochSecond()
                + "}";
    }
}
