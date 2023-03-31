package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("java:S116")
@Getter
@Setter
public class AuthorizeRequestModel {
    @NotNull
    @NotEmpty
    public String response_type;
    @NotNull
    @NotEmpty
    public String client_id;
    @NotNull
    @NotEmpty
    public String redirect_uri;
    public String state;
}