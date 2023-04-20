package com.brew.oauth20.server.manager;

import com.brew.oauth20.server.service.CookieService;
import com.brew.oauth20.server.testUtils.FakerUtils;
import com.brew.oauth20.server.utils.EncryptionUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


//@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCookieManagerTest {
    private static final String USER_COOKIE_KEY = "user";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_SECRET = "jHk$5hVpLm#nG@9$";
    @Mock
    CookieService cookieService;
    @InjectMocks
    UserCookieManager userCookieManager;
    private Faker faker;

    @BeforeEach
    void Setup() {
        Mockito.reset(cookieService);
        userCookieManager.setCookieEncryptionAlgorithm(ENCRYPTION_ALGORITHM);
        userCookieManager.setCookieEncryptionSecret(ENCRYPTION_SECRET);
    }

    @BeforeAll
    void initialize() {
        this.faker = new Faker();
    }

    @Test
    void should_get_user_id_if_valid_cookie_exists() throws Exception {
        // Arrange
        var userId = faker.random().nextLong();
        var expiresAt = faker.date().future(5, TimeUnit.DAYS);
        var request = new MockHttpServletRequest();
        var cookieValue = userId + ":" + expiresAt.toInstant().getEpochSecond();
        var encryptionCookieSecret = FakerUtils.create128BitRandomString(faker);
        var encryptedCookieValue = EncryptionUtils.encrypt(cookieValue, ENCRYPTION_ALGORITHM, encryptionCookieSecret);
        when(cookieService.getCookie(request, USER_COOKIE_KEY))
                .thenReturn(encryptedCookieValue);

        // Act
        var actualUserId = UserCookieManager.getUser(request);

        // Assert
        assertThat(actualUserId).isPresent().contains(userId);

    }

    @Test
    void should_get_null_value_if_cookie_does_not_exist() throws Exception {
        // Arrange
        var request = new MockHttpServletRequest();

        // Act
        var actualUserId = UserCookieManager.getUser(request);

        // Assert
        assertThat(actualUserId).isNotPresent();
    }

    @Test
    void should_get_null_value_if_cookie_is_expired() throws Exception {
        // Arrange
        var request = new MockHttpServletRequest();

        // Act
        var actualUserId = UserCookieManager.getUser(request);

        // Assert
        assertThat(actualUserId).isNotPresent();
    }
}