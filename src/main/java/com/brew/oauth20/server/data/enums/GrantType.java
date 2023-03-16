package com.brew.oauth20.server.data.enums;

public enum GrantType {
    AUTHORIZATION_CODE("authorization_code");

    private final String value;

    GrantType(String value) {
        this.value = value;
    }

    public static GrantType fromValue(String value) {
        for (GrantType grantType : GrantType.values()) {
            if (grantType.getResponseType().equals(value)) {
                return grantType;
            }
        }
        return null;
    }

    public String getResponseType() {
        return value;
    }
}