package com.brew.oauth20.server.component;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;


public interface UserCookieManager {
    Optional<String> getUser(HttpServletRequest request);
}
