package com.brew.oauth20.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
@AllArgsConstructor
public class TokenModel {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("refresh_token_expires_in")
    private long refreshTokenExpiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String state;

    @JsonProperty("token_type")
    private String tokenType;
}