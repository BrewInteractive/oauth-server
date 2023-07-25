package com.brew.oauth20.server.component.impl;

import com.brew.oauth20.server.component.TokenCookieManager;
import com.brew.oauth20.server.model.TokenCookieModel;
import com.brew.oauth20.server.service.CookieService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieManagerImpl implements TokenCookieManager {
    private static final String ACCESS_TOKEN_COOKIE_KEY = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_KEY = "refresh_token";
    @Autowired
    CookieService cookieService;

    @Override
    public void setTokens(HttpServletResponse response, TokenCookieModel tokenCookieModel) {
        cookieService.setCookie(response, ACCESS_TOKEN_COOKIE_KEY, tokenCookieModel.accessToken(), tokenCookieModel.accessTokenExpiresInMinutes());
        cookieService.setCookie(response, REFRESH_TOKEN_COOKIE_KEY, tokenCookieModel.refreshToken(), tokenCookieModel.refreshTokenExpiresInDays() * 24 * 60);
    }
}
