package com.brew.oauth20.server.exception;

public class UserIdentityServiceException
        extends RuntimeException {
    public UserIdentityServiceException(Exception e) {
        super(e);
    }
}
