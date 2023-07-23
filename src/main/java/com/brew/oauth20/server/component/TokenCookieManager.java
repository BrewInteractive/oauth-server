package com.brew.oauth20.server.component;

import com.brew.oauth20.server.model.TokenCookieModel;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenCookieManager {
    void setTokens(HttpServletResponse response, TokenCookieModel tokenCookieModel);
}