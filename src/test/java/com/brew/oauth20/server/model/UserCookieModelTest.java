package com.brew.oauth20.server.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserCookieModelTest {

    private static Stream<Arguments> should_parse_from_string() {

        return Stream.of(
                Arguments.of(
                        "12345:1681809664",
                        new UserCookieModel(12345L, OffsetDateTime.parse("2023-04-18T09:21:04Z"))
                )
        );
    }

    private static Stream<Arguments> should_throw_exception_for_invalid_value_format() {

        return Stream.of(
                Arguments.of(
                        "invalid_value",
                        "12345:"
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

    @ParameterizedTest
    @MethodSource
    void should_throw_exception_for_invalid_value_format(String value) {

        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            UserCookieModel.parse(value);
        });
    }
}