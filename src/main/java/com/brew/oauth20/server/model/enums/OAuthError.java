package com.brew.oauth20.server.model.enums;

@SuppressWarnings("java:S115")
public enum OAuthError {
    INVALID_CLIENT("invalid_client"),
    INVALID_GRANT("invalid_grant"),
    INVALID_REQUEST("invalid_request"),
    INVALID_SCOPE("invalid_scope"),
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),

    SERVER_ERROR("server_error");

    private final String value;

    OAuthError(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}