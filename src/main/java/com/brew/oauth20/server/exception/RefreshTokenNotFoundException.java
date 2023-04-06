package com.brew.oauth20.server.exception;

public class RefreshTokenNotFoundException
        extends RuntimeException {
    public RefreshTokenNotFoundException(String token) {
        super(token);
    }
}