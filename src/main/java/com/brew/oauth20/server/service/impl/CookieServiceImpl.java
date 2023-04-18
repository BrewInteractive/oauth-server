package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieServiceImpl implements CookieService {

    private static final Integer DEFAULT_COOKIE_EXPIRE_IN_MINUTES = 30;

    @Override
    public void deleteCookie(HttpServletResponse response, String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public void setCookie(HttpServletResponse response, String key, String value, int expiresInMin, boolean isHttpOnly, boolean isSecure, String path) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiresInMin * 60);
        cookie.setSecure(isSecure);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    @Override
    public void setCookie(HttpServletResponse response, String key, String value, int expiresInMin, boolean isHttpOnly, boolean isSecure) {
        this.setCookie(response, key, value, expiresInMin, isHttpOnly, isSecure, "/");
    }

    @Override
    public void setCookie(HttpServletResponse response, String key, String value, int expiresInMin, boolean isHttpOnly) {
        this.setCookie(response, key, value, expiresInMin, isHttpOnly, false, "/");
    }

    @Override
    public void setCookie(HttpServletResponse response, String key, String value, int expiresInMin) {
        this.setCookie(response, key, value, expiresInMin, false, false, "/");
    }

    @Override
    public void setCookie(HttpServletResponse response, String key, String value) {
        this.setCookie(response, key, value, DEFAULT_COOKIE_EXPIRE_IN_MINUTES, false, false, "/");
    }

    @Override
    public String getCookie(HttpServletRequest request, String key) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> c.getName().equals(key))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElse(null);
    }
}
