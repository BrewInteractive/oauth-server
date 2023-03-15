package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.service.UserCookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserCookieServiceImpl implements UserCookieService {
    @Override
    public void deleteUserCookie(HttpServletResponse response, String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly, boolean isSecure, String path) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAgeMinuteCount * 60);
        cookie.setSecure(isSecure);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly, boolean isSecure) {
        this.setUserCookie(response, key, value, maxAgeMinuteCount, isHttpOnly, isSecure, "/");
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount, boolean isHttpOnly) {
        this.setUserCookie(response, key, value, maxAgeMinuteCount, isHttpOnly, false, "/");
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value, int maxAgeMinuteCount) {
        this.setUserCookie(response, key, value, maxAgeMinuteCount, false, false, "/");
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value) {
        this.setUserCookie(response, key, value, 30, false, false, "/");
    }

    @Override
    public String getUserCookie(HttpServletRequest request, String key) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(key))
                return c.getValue();
        }
        return null;
    }
}
