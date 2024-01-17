package com.brew.oauth20.server.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserCookieModelTest {

    private static Stream<Arguments> should_parse_from_string() {
        var date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1681809664), ZoneOffset.UTC);
        var model = new UserCookieModel("12345", date, "email@test.com");
        var model2 = new UserCookieModel("54321", date, null);
        var model3 = new UserCookieModel("54321", date, "test@email.com");
        var string = UserCookieModel.toString(model);
        return Stream.of(
                Arguments.of(string, model),
                Arguments.of("{"
                        + "\"user_id\": \"" + model2.user_id() + "\","
                        + "\"expires_at\": " + date.toEpochSecond()
                        + "}", model2),
                Arguments.of("{"
                        + "\"user_id\": \"" + model3.user_id() + "\","
                        + "\"email\": \"" + model3.email() + "\","
                        + "\"expires_at\": " + date.toEpochSecond()
                        + "}", model3),
                Arguments.of("", null),
                Arguments.of(null, null)
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
        assertThrows(IllegalArgumentException.class, () -> UserCookieModel.parse(value));
    }
}