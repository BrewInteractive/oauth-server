package com.brew.oauth20.server.component;

import com.brew.oauth20.server.component.impl.TokenCookieManagerImpl;
import com.brew.oauth20.server.model.TokenCookieModel;
import com.brew.oauth20.server.service.CookieService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenCookieManagerTest {
    private static final String ACCESS_TOKEN_COOKIE_KEY = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_KEY = "access_token";
    private static Faker faker;
    @Mock
    CookieService cookieService;
    @InjectMocks
    TokenCookieManagerImpl tokenCookieManager;

    @BeforeAll
    void initialize() {
        faker = new Faker();
    }

    @Test
    void should_set_tokens() {
        // Arrange
        TokenCookieModel tokenCookieModel = TokenCookieModel.builder()
                .accessToken(faker.letterify("?".repeat(32)))
                .refreshToken(faker.letterify("?".repeat(64)))
                .accessTokenExpiresInMinutes(30)
                .refreshTokenExpiresInDays(7)
                .build();
        var response = new MockHttpServletResponse();

        // Capture the arguments passed to the cookieService.setCookie method
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> expiresInMinCaptor = ArgumentCaptor.forClass(Integer.class);

        // Act
        tokenCookieManager.setTokens(response, tokenCookieModel);

        // Assert
        verify(cookieService, times(2)).setCookie(eq(response), keyCaptor.capture(), valueCaptor.capture(), expiresInMinCaptor.capture());
        List<String> keys = keyCaptor.getAllValues();
        List<String> values = valueCaptor.getAllValues();
        List<Integer> expiresInMins = expiresInMinCaptor.getAllValues();

        // Verify that the correct arguments were passed to the setCookie method
        assertThat(keys).asList().contains(ACCESS_TOKEN_COOKIE_KEY, REFRESH_TOKEN_COOKIE_KEY);
        assertThat(values).asList().contains(tokenCookieModel.accessToken(), tokenCookieModel.refreshToken());
        assertThat(expiresInMins).asList().contains(tokenCookieModel.accessTokenExpiresInMinutes(), tokenCookieModel.refreshTokenExpiresInDays() * 24 * 60);
    }
}