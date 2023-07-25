package com.brew.oauth20.server.exception;

public class ClientsUserNotFoundException
        extends RuntimeException {
    public ClientsUserNotFoundException(String clientId, String userId) {
        super(clientId + "," + userId);
    }
}
