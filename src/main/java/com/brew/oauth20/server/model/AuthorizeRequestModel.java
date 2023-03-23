package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
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
