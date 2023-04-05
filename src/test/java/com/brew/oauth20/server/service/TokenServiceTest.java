package com.brew.oauth20.server.service;

import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.service.impl.TokenServiceImpl;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private static ClientModelFixture clientModelFixture;
    private static Faker faker;

    @BeforeAll
    public static void init() {
        faker = new Faker();
        clientModelFixture = new ClientModelFixture();
    }

    private static Stream<Arguments> should_generate_token_without_refresh_token() {

        return Stream.of(
                Arguments.of(
                        clientModelFixture.createRandomOne(false),
                        faker.random().nextLong(),
                        faker.lordOfTheRings().location()
                )
        );
    }

    private static Stream<Arguments> should_generate_token_with_refresh_token() {

        return Stream.of(
                Arguments.of(
                        clientModelFixture.createRandomOne(true),
                        faker.random().nextLong(),
                        faker.lordOfTheRings().location()
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void should_generate_token_without_refresh_token(ClientModel client, Long userId, String state) {

        // Act
        var result = new TokenServiceImpl().generateToken(client, userId, state);

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
    void should_generate_token_with_refresh_token(ClientModel client, Long userId, String state, String refreshToken) {
        // Act
        var result = new TokenServiceImpl().generateToken(client, userId, state);

        // Assert
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isPositive();
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
    }
}