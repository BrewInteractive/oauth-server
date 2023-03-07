package com.brew.oauth20.server.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class AuthorizeRequest {
    @NotEmpty
    public String response_type;

    @NotEmpty
    public String client_id;

    @NotEmpty
    public String redirect_uri;
    public String state;
}
