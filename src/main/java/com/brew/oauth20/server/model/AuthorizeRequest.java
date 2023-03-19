package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorizeRequest {
    @NotEmpty
    @NotNull
    public String response_type;
    @NotEmpty
    @NotNull
    public String client_id;
    @NotEmpty
    @NotNull
    public String redirect_uri;
    public String state;
}
