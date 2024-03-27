package com.brew.oauth20.server.exception;

import com.brew.oauth20.server.model.enums.OAuthError;

public class UnsupportedGrantTypeException
        extends RuntimeException {
    public UnsupportedGrantTypeException() {
        super(OAuthError.UNSUPPORTED_GRANT_TYPE.getValue());
    }
}