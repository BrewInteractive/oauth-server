package com.brew.oauth20.server.model;

import com.brew.oauth20.server.utils.validators.constraints.ValidateTokenRequestModel;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"java:S116", "java:S1104"})
@Getter
@Setter
@Builder
@ValidateTokenRequestModel
@NotNull
public class TokenRequestModel {
    @NotNull
    @NotEmpty
    public String grant_type;
    public String redirect_uri;
    public String client_id;
    public String client_secret;
    public String code;
    public String refresh_token;
    public String state;
}
