package com.brew.oauth20.server.utils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UriUtils {
    private UriUtils() {
    }

    public static boolean isValidUrl(String url) throws PatternSyntaxException, NullPointerException {
        try {
            return java.util.regex.Pattern.compile("\\b(?:(https?|ftp|file)://|www\\.)?[-A-Z0-9+&#/%?=~_|$!:,.;]*[A-Z0-9+&@#/%=~_|$]\\.[-A-Z0-9+&@#/%?=~_|$!:,.;]*[A-Z0-9+&@#/%=~_|$]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(url).matches();
        } catch (PatternSyntaxException | NullPointerException ex) {
            return false;
        }
    }
}
