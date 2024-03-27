package com.brew.oauth20.server.model.enums;

@SuppressWarnings("java:S115")
public enum OAuthError {
    INVALID_GRANT("invalid_grant"),
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),

    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),
    INVALID_SCOPE("invalid_scope");

    private final String value;

    OAuthError(String value) {
        this.value = value;
    }

    public static OAuthError fromValue(String value) {
        for (OAuthError oAuthError : OAuthError.values()) {
            if (oAuthError.getValue().equalsIgnoreCase(value)) {
                return oAuthError;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}