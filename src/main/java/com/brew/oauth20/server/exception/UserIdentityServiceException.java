package com.brew.oauth20.server.exception;

public class UserIdentityServiceException
        extends RuntimeException {
    public UserIdentityServiceException(Exception innerException) {
        super("Error while fetching custom claims from user identity service: ", innerException);
    }
}
