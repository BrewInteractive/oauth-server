package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateClientLogoRequestModel(@NotNull @NotEmpty @NotBlank String logoFile) {
}