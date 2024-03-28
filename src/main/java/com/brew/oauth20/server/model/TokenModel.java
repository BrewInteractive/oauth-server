package com.brew.oauth20.server.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;


@Builder
@Getter
@Setter
@AllArgsConstructor
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenModel {
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private String state;
    private String tokenType;
    private long expiresIn;
}