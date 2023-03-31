package com.brew.oauth20.server.exception;

public class MissingServiceException
        extends RuntimeException {
    public MissingServiceException(RuntimeException innerException) {
        super(innerException);
    }
}


