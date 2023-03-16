package com.brew.oauth20.server.data.enums;

public enum GrantType {
    AUTHORIZATION_CODE("authorization_code");

    private final String grantType;

    GrantType(String grantType) {
        this.grantType = grantType;
    }

    public static GrantType fromString(String grantTypeString) {
        for (GrantType grantType : GrantType.values()) {
            if (grantType.getResponseType().equals(grantTypeString)) {
                return grantType;
            }
        }
        return null;
    }

    public String getResponseType() {
        return grantType;
    }
}