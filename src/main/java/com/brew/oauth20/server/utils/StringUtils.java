package com.brew.oauth20.server.utils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    private static final String DEFAULT_GENERATOR_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private StringUtils() {
    }

    public static String generateSecureRandomString() {
        int length = 32;
        return generate(length, DEFAULT_GENERATOR_CHARS);
    }

    public static String generateSecureRandomString(int length) {
        return generate(length, DEFAULT_GENERATOR_CHARS);
    }

    public static String generateSecureRandomString(String chars) {
        int length = 32;
        return generate(length, chars);
    }

    public static String generateSecureRandomString(int length, String chars) {
        return generate(length, chars);
    }

    private static String generate(int length, String chars) {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.ints(length, 0, chars.length())
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    public static Map<String, String> parseCookieString(String cookieString) {
        Map<String, String> cookieMap = new HashMap<>();
        String[] keyValuePairs = cookieString.split(":");
        for (String pair : keyValuePairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                cookieMap.put(key, value);
            } else
                throw new IllegalArgumentException("Invalid value format: " + pair);
        }
        return cookieMap;
    }
}
