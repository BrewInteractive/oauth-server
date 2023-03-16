package com.brew.oauth20.server.data.enums;

public enum ResponseType {
    CODE("code"),
    TOKEN("token");

    private final String value;

    ResponseType(String value) {
        this.value = value;
    }

    public static ResponseType fromValue(String value) {
        for (ResponseType responseType : ResponseType.values()) {
            if (responseType.getResponseType().equals(value)) {
                return responseType;
            }
        }
        return null;
    }

    public String getResponseType() {
        return value;
    }
}