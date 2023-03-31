package com.brew.oauth20.server.exception;

public class UnsupportedServiceTypeException
        extends RuntimeException {
    public UnsupportedServiceTypeException(String serviceProviderType) {
        super(serviceProviderType);
    }

    public UnsupportedServiceTypeException() {
        super();
    }
}

