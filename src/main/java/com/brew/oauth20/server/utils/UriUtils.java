package com.brew.oauth20.server.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UriUtils {
    private UriUtils() {
    }

    public static boolean isValidUrl(String url) throws PatternSyntaxException, NullPointerException {
        try {
            Pattern regex = Pattern.compile("\\b(?:(https?|ftp|file)://|www\\.)?[-A-Z0-9+&#/%?=~_|$!:,.;]*[A-Z0-9+&@#/%=~_|$]\\.[-A-Z0-9+&@#/%?=~_|$!:,.;]*[A-Z0-9+&@#/%=~_|$]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(url);
            return regexMatcher.matches();
        } catch (PatternSyntaxException | NullPointerException ex) {
            return false;
        }
    }
}
