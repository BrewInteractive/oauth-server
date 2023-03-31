package com.brew.oauth20.server.data.enums;

@SuppressWarnings("java:S115")
public enum ResponseType {
    code("code"),
    token("token");

    private final String value;

    ResponseType(String value) {
        this.value = value;
    }

    public static ResponseType fromValue(String value) {
        for (ResponseType responseType : ResponseType.values()) {
            if (responseType.getResponseType().equalsIgnoreCase(value)) {
                return responseType;
            }
        }
        return null;
    }

    public String getResponseType() {
        return value;
    }
}