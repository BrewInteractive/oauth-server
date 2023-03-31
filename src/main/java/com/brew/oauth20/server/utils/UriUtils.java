package com.brew.oauth20.server.utils;

import java.util.regex.PatternSyntaxException;

public class UriUtils {
    private UriUtils() {
    }

    public static boolean isValidUrl(String url) throws PatternSyntaxException, NullPointerException {
        try {
            return java.util.regex.Pattern.compile("^(?i)(?:(?:https?|ftp|file)://|www\\.)[-\\w+&@#/%?=~_|!:,.;]*[-\\w+&@#/%=~_|]$").matcher(url).matches();
        } catch (PatternSyntaxException | NullPointerException ex) {
            return false;
        }
    }
}
