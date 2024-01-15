package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.service.impl.JwtServiceImpl;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtServiceTest {
    private static Faker faker;

    @BeforeAll
    public static void init() {
        faker = new Faker();
    }

    private static Stream<Arguments> should_sign_token_without_subject() {
        return Stream.of(
                Arguments.of(
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.lordOfTheRings().character(),
                        faker.random().nextInt(Integer.MAX_VALUE),
                        new HashMap<String, Object>() {{
                            put("additional_value", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                        }}
                ),
                Arguments.of(
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.lordOfTheRings().character(),
                        faker.random().nextInt(Integer.MAX_VALUE),
                        null
                )
        );
    }

    private static Stream<Arguments> should_sign_token_with_subject() {
        return Stream.of(
                Arguments.of(
                        String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)),
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.lordOfTheRings().character(),
                        faker.random().nextInt(Integer.MAX_VALUE),
                        new HashMap<String, Object>() {{
                            put("client_id", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                            put("user_id", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                        }}
                ),
                Arguments.of(
                        String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)),
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.lordOfTheRings().character(),
                        faker.random().nextInt(Integer.MAX_VALUE),
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void should_sign_token_without_subject(String audience,
                                           String issuerUri,
                                           String state,
                                           Integer tokenExpiresInMinutes,
                                           HashMap<String, Object> additionalClaims) {

        // Act
        var jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", faker.letterify("?".repeat(32)));
        var result = jwtService.signToken(new SignTokenOptions(null, audience, issuerUri, state, tokenExpiresInMinutes, additionalClaims));

        // Assert
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isPositive();
        assertThat(result.getRefreshToken()).isBlank();
    }

    @ParameterizedTest
    @MethodSource
    void should_sign_token_with_subject(String subject,
                                        String audience,
                                        String issuerUri,
                                        String state,
                                        Integer tokenExpiresInMinutes,
                                        HashMap<String, Object> additionalClaims) {

        // Act
        var jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", faker.letterify("?".repeat(32)));
        var result = jwtService.signToken(new SignTokenOptions(subject, audience, issuerUri, state, tokenExpiresInMinutes, additionalClaims));

        // Assert
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isPositive();
        assertThat(result.getRefreshToken()).isBlank();
    }
}