package com.brew.oauth20.server.component;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserCookieManager {
    Optional<Long> getUser(HttpServletRequest request);
}
