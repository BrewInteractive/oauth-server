package com.brew.oauth20.server.model;

import com.brew.oauth20.server.utils.validators.constraints.ValidateTokenRequestModel;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"java:S116", "java:S1104"})
@Data
@Builder
@ValidateTokenRequestModel
@NotNull
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenRequestModel {
    @NotNull
    @NotEmpty
    private String grantType;
    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String code;
    private String refreshToken;
    private String state;
}
