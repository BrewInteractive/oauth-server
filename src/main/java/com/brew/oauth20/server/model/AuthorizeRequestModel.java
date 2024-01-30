package com.brew.oauth20.server.model;

import com.brew.oauth20.server.utils.validators.constraints.UriConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings({"java:S116", "java:S1104"})
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
    @UriConstraint
    public String redirect_uri;
    public String state;
    public String scope;
}
