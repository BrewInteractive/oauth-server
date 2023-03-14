package com.brew.oauth20.server.exception;

public class AuthorizationCodeNotFoundException
        extends RuntimeException {
    public AuthorizationCodeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
