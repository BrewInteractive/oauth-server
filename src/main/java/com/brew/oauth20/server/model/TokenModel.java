package com.brew.oauth20.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TokenModel(@JsonProperty("access_token") String accessToken,
                         @JsonProperty("expires_in") int expiresIn,
                         @JsonProperty("refresh_token") String refreshToken,
                         String state,
                         @JsonProperty("token_type") String tokenType) {
}
