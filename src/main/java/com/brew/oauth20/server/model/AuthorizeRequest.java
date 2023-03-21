package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
@Builder
public class AuthorizeRequest {
    @NotNull
    @NotEmpty
    public String response_type;
    @NotNull
    public UUID client_id;
    @NotNull
    @NotEmpty
    public String redirect_uri;
    public String state;
}
