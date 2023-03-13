package com.brew.oauth20.server.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthorizeRequest {
    @NotEmpty(message = "The response_type is required.")
    public String response_type;

    @NotEmpty(message = "The client_id is required.")
    public String client_id;

    @NotEmpty(message = "The redirect_uri is required.")
    public String redirect_uri;
    public String state;
}
