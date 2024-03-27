package com.brew.oauth20.server.exception;

import com.brew.oauth20.server.model.enums.OAuthError;

public class OAuthException
        extends RuntimeException {
    public OAuthException(OAuthError error) {
        super(error.getValue());
    }
}