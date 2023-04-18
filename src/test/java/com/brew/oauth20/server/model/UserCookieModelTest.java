package com.brew.oauth20.server.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class UserCookieModelTest {

    private static Stream<Arguments> should_parse_from_string() {

        return Stream.of(
                Arguments.of(
                        "12345:1681809664",
                        new UserCookieModel(12345L, OffsetDateTime.parse("2023-04-18T12:21:04.132176+03:00"))
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void should_parse_from_string(String value, UserCookieModel expectedUserCookieModel) {
        // Act
        var actualUserCookieModel = UserCookieModel.parse(value);

        // Assert
        assertThat(actualUserCookieModel).isEqualTo(expectedUserCookieModel);

    }
}