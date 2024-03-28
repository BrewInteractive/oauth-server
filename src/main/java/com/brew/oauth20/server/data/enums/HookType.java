package com.brew.oauth20.server.data.enums;

@SuppressWarnings("java:S115")
public enum HookType {
    custom_claim("custom_claim");

    private final String value;

    HookType(String value) {
        this.value = value;
    }

    public static HookType fromValue(String value) {
        for (HookType hookType : HookType.values()) {
            if (hookType.getHookType().equalsIgnoreCase(value)) {
                return hookType;
            }
        }
        return null;
    }

    public String getHookType() {
        return value;
    }
}