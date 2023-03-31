package com.brew.oauth20.server.data.enums;

@SuppressWarnings("java:S115")
public enum GrantType {
    authorization_code("authorization_code"),
    refresh_token("refresh_token"),
    client_credentials("client_credentials");

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