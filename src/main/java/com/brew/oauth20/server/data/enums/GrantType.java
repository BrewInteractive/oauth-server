package com.brew.oauth20.server.data.enums;

public enum GrantType {
    authorization_code("authorization_code");

    private final String value;

    GrantType(String value) {
        this.value = value;
    }

    public static GrantType fromValue(String value) {
        for (GrantType grantType : GrantType.values()) {
            if (grantType.getGrantType().equalsIgnoreCase(value)) {
                return grantType;
            }
        }
        return null;
    }

    public String getGrantType() {
        return value;
    }
}