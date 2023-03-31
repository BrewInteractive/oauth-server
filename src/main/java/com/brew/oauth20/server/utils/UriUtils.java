package com.brew.oauth20.server.utils;

import java.util.regex.PatternSyntaxException;


public class UriUtils {
    private UriUtils() {
    }

    public static boolean isValidUrl(String url) {
        try {
            return java.util.regex.Pattern.compile("^(?i)(?:https?://)?(?:www\\.)?([a-z0-9]+(?:\\.[a-z0-9]+)+)(?::\\d{1,5})?(?:/[\\w#!:.?+=&%@!\\-/]+)?$").matcher(url).matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }
}
