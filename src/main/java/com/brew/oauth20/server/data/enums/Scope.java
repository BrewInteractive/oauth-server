package com.brew.oauth20.server.data.enums;

@SuppressWarnings("java:S115")
public enum Scope {
    openid("openid"),
    profile("profile"),
    email("email");

    private final String value;

    Scope(String value) {
        this.value = value;
    }

    public static Scope fromValue(String value) {
        for (Scope scope : Scope.values()) {
            if (scope.getScope().equalsIgnoreCase(value)) {
                return scope;
            }
        }
        return null;
    }

    public String getScope() {
        return value;
    }
}