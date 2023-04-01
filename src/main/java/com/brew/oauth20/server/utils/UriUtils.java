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

            if (host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                return false;
            }

            if (!scheme.equals("http") && !scheme.equals("https")) {
                return false;
            }


            String[] parts = host.split("\\.");
            if (parts.length == 1) {
                return false;
            }

            for (String part : parts) {
                if (!part.matches("^[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]$")) {
                    return false;
                }
            }

            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}