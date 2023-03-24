package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class TokenRequestModel {
    @NotNull
    @NotEmpty
    public String grant_type;
    @NotNull
    @NotEmpty
    public String redirect_uri;
    public String client_id;
    public String client_secret;
    public String code;
    public String refresh_token;
    public String state;
}
