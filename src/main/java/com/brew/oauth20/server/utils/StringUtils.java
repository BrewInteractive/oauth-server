package com.brew.oauth20.server.utils;

import java.security.SecureRandom;

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
}
