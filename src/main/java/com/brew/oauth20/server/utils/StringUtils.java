package com.brew.oauth20.server.utils;

import java.security.SecureRandom;

public class StringUtils {
    public static  String GenerateSecureRandomString() {
        int length = 32;
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.ints(length, 0, chars.length())
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }
}
