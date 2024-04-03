package com.brew.oauth20.server.model;

import com.brew.oauth20.server.utils.validators.constraints.ValidateTokenRequestModel;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@ValidateTokenRequestModel
@NotNull
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomClaimsRequestModel {
    private String userId;
}
