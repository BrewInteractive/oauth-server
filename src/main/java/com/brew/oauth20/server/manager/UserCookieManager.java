package com.brew.oauth20.server.manager;

import com.brew.oauth20.server.model.UserCookieModel;
import com.brew.oauth20.server.service.CookieService;
import com.brew.oauth20.server.utils.EncryptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.OffsetDateTime;
import java.util.Optional;

public class UserCookieManager {

    private static final String USER_COOKIE_KEY = "user";
    @Autowired
    static CookieService cookieService;
    @Value("${cookie.encryption.algorithm}")
    private static String cookieEncryptionAlgorithm;
    @Value("${cookie.encryption.secret}")
    private static String cookieEncryptionSecret;

    public static Optional<Long> getUser(HttpServletRequest request) throws Exception {
        var cookieValue = cookieService.getCookie(request, USER_COOKIE_KEY);

        if (cookieValue.isBlank())
            return Optional.empty();

        var decryptedCookieValue = EncryptionUtils.decrypt(cookieValue, cookieEncryptionAlgorithm, cookieEncryptionSecret);
        var userCookieModel = UserCookieModel.parse(decryptedCookieValue);

        if (userCookieModel.expiresAt().isBefore(OffsetDateTime.now()))
            return Optional.empty();

        return Optional.of(userCookieModel.userId());
    }
}
