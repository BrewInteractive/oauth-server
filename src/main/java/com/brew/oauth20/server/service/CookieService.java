package com.brew.oauth20.server.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    void setCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly, boolean isSecure, String path);

    void setCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly, boolean isSecure);

    void setCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly);

    void setCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount);

    void setCookie(HttpServletResponse response, String key, String value);

    void deleteCookie(HttpServletResponse response, String key);

    String getCookie(HttpServletRequest request, String key);
}
