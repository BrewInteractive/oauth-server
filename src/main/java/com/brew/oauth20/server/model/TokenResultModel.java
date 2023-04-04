package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"java:S116", "java:S1104"})
@Getter
@Setter
public class TokenResultModel {
    @NotNull
    @NotEmpty
    public String access_token;
    @NotNull
    @NotEmpty
    public String refresh_token;
    public int expires_in;
    public String state;
    public String token_type;
}
