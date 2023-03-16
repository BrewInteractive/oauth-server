package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.service.UserCookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserCookieServiceImpl implements UserCookieService {

    private static final Integer DEFAULT_COOKIE_EXPIRE_IN_MINUTES = 30;

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
    public void setUserCookie(HttpServletResponse response, String key, String value, int expiresInMin, boolean isHttpOnly, boolean isSecure, String path) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiresInMin * 60);
        cookie.setSecure(isSecure);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value, int expiresInMin, boolean isHttpOnly, boolean isSecure) {
        this.setUserCookie(response, key, value, expiresInMin, isHttpOnly, isSecure, "/");
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value, int expiresInMin, boolean isHttpOnly) {
        this.setUserCookie(response, key, value, expiresInMin, isHttpOnly, false, "/");
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value, int expiresInMin) {
        this.setUserCookie(response, key, value, expiresInMin, false, false, "/");
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String key, String value) {
        this.setUserCookie(response, key, value, DEFAULT_COOKIE_EXPIRE_IN_MINUTES, false, false, "/");
    }

    @Override
    public String getUserCookie(HttpServletRequest request, String key) {
        var cookies = request.getCookies();
        if (cookies == null)
            return null;
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(key))
                return c.getValue();
        }
        return null;
    }
}
