package com.brew.oauth20.server.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UriUtils {
    private UriUtils() {
    }

    public static boolean isValidUrl(String url) throws MalformedURLException {
        try {
            // it will check only for scheme and not null input
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
