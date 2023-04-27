package com.brew.oauth20.server.component;

import com.brew.oauth20.server.component.impl.UserCookieManagerImpl;
import com.brew.oauth20.server.service.CookieService;
import com.brew.oauth20.server.utils.EncryptionUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCookieManagerTest {
    private static final String USER_COOKIE_KEY = "user";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_SECRET = "jHk$5hVpLm#nG@9$";
    private static Faker faker;
    @Mock
    CookieService cookieService;
    @InjectMocks
    UserCookieManagerImpl userCookieManager;

    private static Stream<Arguments> should_get_null_value_if_cookie_does_not_exist() {

        return Stream.of(
                Arguments.of(
                        null,
                        ""
                )
        );
    }

    @BeforeAll
    void initialize() {
        faker = new Faker();

    }

    @BeforeEach
    void Setup() {
        Mockito.reset(cookieService);
        ReflectionTestUtils.setField(userCookieManager, "cookieEncryptionSecret", ENCRYPTION_SECRET);
        ReflectionTestUtils.setField(userCookieManager, "cookieEncryptionAlgorithm", ENCRYPTION_ALGORITHM);
    }

    @Test
    void should_get_user_id_if_valid_cookie_exists() throws Exception {
        // Arrange
        var userId = faker.random().nextLong();
        var expiresAt = faker.date().future(5, TimeUnit.DAYS);
        var request = new MockHttpServletRequest();
        var cookieValue = userId + ":" + expiresAt.toInstant().getEpochSecond();
        var encryptedCookieValue = EncryptionUtils.encrypt(cookieValue, ENCRYPTION_ALGORITHM, ENCRYPTION_SECRET);
        when(cookieService.getCookie(request, USER_COOKIE_KEY))
                .thenReturn(encryptedCookieValue);

        // Act
        var actualUserId = userCookieManager.getUser(request);

        // Assert
        assertThat(actualUserId).isPresent().contains(userId);

    }

    @MethodSource
    @ParameterizedTest
    void should_get_null_value_if_cookie_does_not_exist(String cookieValue) {
        // Arrange
        var request = new MockHttpServletRequest();
        when(cookieService.getCookie(request, USER_COOKIE_KEY))
                .thenReturn(cookieValue);

        // Act
        var actualUserId = userCookieManager.getUser(request);

        // Assert
        assertThat(actualUserId).isNotPresent();
    }


    @Test
    void should_get_null_value_if_cookie_is_expired() throws Exception {
        // Arrange
        var request = new MockHttpServletRequest();
        var userId = faker.random().nextLong();
        var expiresAt = faker.date().past(5, TimeUnit.DAYS);
        var cookieValue = userId + ":" + expiresAt.toInstant().getEpochSecond();
        var encryptedCookieValue = EncryptionUtils.encrypt(cookieValue, ENCRYPTION_ALGORITHM, ENCRYPTION_SECRET);
        when(cookieService.getCookie(request, USER_COOKIE_KEY))
                .thenReturn(encryptedCookieValue);

        // Act
        var actualUserId = userCookieManager.getUser(request);

        // Assert
        assertThat(actualUserId).isNotPresent();
    }

    @Test
    void should_get_null_value_if_exception_thrown() {
        // Arrange
        var request = new MockHttpServletRequest();
        when(cookieService.getCookie(request, USER_COOKIE_KEY))
                .thenReturn("123");

        // Act
        var actualUserId = userCookieManager.getUser(request);

        // Assert
        assertThat(actualUserId).isNotPresent();
    }
}