package com.brew.oauth20.server.data.enums;

public enum ResponseType {
    code("code"),
    token("token");

    private final String responseType;

    ResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getResponseType() {
        return responseType;
    }
}