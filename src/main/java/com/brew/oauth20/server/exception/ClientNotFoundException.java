package com.brew.oauth20.server.exception;

public class ClientNotFoundException
        extends RuntimeException {
    public ClientNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}