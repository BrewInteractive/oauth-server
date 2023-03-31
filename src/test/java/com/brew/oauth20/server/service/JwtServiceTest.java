package com.brew.oauth20.server.service;

import com.brew.oauth20.server.service.impl.JwtServiceImpl;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private static Faker faker;

    @BeforeAll
    public static void init() {
        faker = new Faker();
    }

    private static Stream<Arguments> should_sign_token() {
        return Stream.of(
                Arguments.of(
                        String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)),
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.lordOfTheRings().character(),
                        faker.random().nextInt(Integer.MAX_VALUE),
                        faker.letterify("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"),
                        new HashMap<String, Object>() {{
                            put("client_id", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                            put("user_id", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                        }}
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void should_sign_token(String subject,
                           String audience,
                           String issuerUri,
                           String state,
                           Integer tokenExpiresInMinutes,
                           String signingKey,
                           HashMap<String, Object> additionalClaims) {

        // Act
        var result = new JwtServiceImpl().signToken(subject, audience, issuerUri, state, tokenExpiresInMinutes, signingKey, additionalClaims);

        // Assert
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
    }
}