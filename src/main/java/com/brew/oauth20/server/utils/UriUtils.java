package com.brew.oauth20.server.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {

    private UriUtils(){}

    public static boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);

            String scheme = uri.getScheme();
            if (scheme == null) {
                // If no scheme is specified, assume it is either "http" or "https"
                return isValidUrl("http://" + url) || isValidUrl("https://" + url);
            }

            String host = uri.getHost();
            if (host == null) {
                return false;
            }

            if (!scheme.equals("http") && !scheme.equals("https")) {
                return false;
            }

            String[] parts = host.split("\\.");
            return parts.length != 1;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}