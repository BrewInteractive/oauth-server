package com.brew.oauth20.server.component.impl;

import com.brew.oauth20.server.component.UserCookieManager;
import com.brew.oauth20.server.model.UserCookieModel;
import com.brew.oauth20.server.service.CookieService;
import com.brew.oauth20.server.utils.EncryptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class UserCookieManagerImpl implements UserCookieManager {
    private static final String USER_COOKIE_KEY = "user";
    @Autowired
    CookieService cookieService;
    @Value("${cookie.encryption.secret}")
    String cookieEncryptionSecret;
    @Value("${cookie.encryption.algorithm}")
    String cookieEncryptionAlgorithm;

    public Optional<Long> getUser(HttpServletRequest request) {
        try {
            var cookieValue = cookieService.getCookie(request, USER_COOKIE_KEY);
            if (cookieValue == null || cookieValue.isBlank())
                return Optional.empty();

            var decryptedCookieValue = EncryptionUtils.decrypt(cookieValue, cookieEncryptionAlgorithm, cookieEncryptionSecret);
            var userCookieModel = UserCookieModel.parse(decryptedCookieValue);
            if (userCookieModel.expires_at().isBefore(OffsetDateTime.now()))
                return Optional.empty();

            return Optional.of(userCookieModel.user_id());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
