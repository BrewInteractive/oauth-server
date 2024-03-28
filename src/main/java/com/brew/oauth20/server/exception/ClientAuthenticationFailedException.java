package com.brew.oauth20.server.exception;

public class ClientAuthenticationFailedException
        extends RuntimeException {
    public ClientAuthenticationFailedException() {
        super("Client authentication is failed.");
    }
}

