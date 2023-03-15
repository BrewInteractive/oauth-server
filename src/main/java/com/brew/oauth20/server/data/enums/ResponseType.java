package com.brew.oauth20.server.data.enums;

public enum ResponseType {
    CODE("code"),
    TOKEN("token");

    private final String responseType;

    ResponseType(String responseType) {
        this.responseType = responseType;
    }

    public static ResponseType fromString(String responseTypeString) {
        for (ResponseType responseType : ResponseType.values()) {
            if (responseType.getResponseType().equals(responseTypeString)) {
                return responseType;
            }
        }
        return null;
    }

    public String getResponseType() {
        return responseType;
    }
}