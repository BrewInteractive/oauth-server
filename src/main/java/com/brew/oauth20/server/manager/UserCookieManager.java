package com.brew.oauth20.server.manager;

import com.brew.oauth20.server.model.UserCookieModel;
import com.brew.oauth20.server.service.CookieService;
import com.brew.oauth20.server.utils.EncryptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@SuppressWarnings("java:S3010")
public class UserCookieManager {
    private static final String USER_COOKIE_KEY = "user";

    static CookieService cookieService;

    static String cookieEncryptionAlgorithm;
    static String cookieEncryptionSecret;


    @Autowired
    public UserCookieManager(CookieService cookieService) {
        UserCookieManager.cookieService = cookieService;

    }


    public static Optional<Long> getUser(HttpServletRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var cookieValue = cookieService.getCookie(request, USER_COOKIE_KEY);

        if (cookieValue.isBlank())
            return Optional.empty();

        var decryptedCookieValue = EncryptionUtils.decrypt(cookieValue, cookieEncryptionAlgorithm, cookieEncryptionSecret);
        var userCookieModel = UserCookieModel.parse(decryptedCookieValue);

        if (userCookieModel.expiresAt().isBefore(OffsetDateTime.now()))
            return Optional.empty();

        return Optional.of(userCookieModel.userId());
    }

    @Value("${cookie.encryption.secret}")
    public static void setCookieEncryptionSecret(String secret) {
        UserCookieManager.cookieEncryptionSecret = secret;
    }

    @Value("${cookie.encryption.algorithm}")
    public static void setCookieEncryptionAlgorithm(String algorithm) {
        UserCookieManager.cookieEncryptionAlgorithm = algorithm;
    }


}
