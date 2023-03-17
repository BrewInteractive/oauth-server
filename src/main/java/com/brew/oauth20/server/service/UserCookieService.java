package com.brew.oauth20.server.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserCookieService {
    void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly, boolean isSecure, String path);

    void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly, boolean isSecure);

    void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly);

    void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount);

    void setUserCookie(HttpServletResponse response, String key, String value);

    void deleteUserCookie(HttpServletResponse response, String key);

    String getUserCookie(HttpServletRequest request, String key);
}
