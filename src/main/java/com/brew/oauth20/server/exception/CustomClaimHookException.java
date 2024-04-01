package com.brew.oauth20.server.exception;

public class CustomClaimHookException
        extends RuntimeException {
    public CustomClaimHookException(Exception innerException) {
        super("Error while fetching custom claims from custom claim hook: ", innerException);
    }
}
