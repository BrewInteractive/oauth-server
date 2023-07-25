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
        var date =  OffsetDateTime.ofInstant(Instant.ofEpochSecond(1681809664), ZoneOffset.UTC);
        return Stream.of(
                Arguments.of(
                        "user_id=12345:email=email@test.com:country_code=0090:phone_number=12345667:expires_at=1681809664",
                        new UserCookieModel("12345", date,"email@test.com","0090","12345667")
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
        assertThrows(IllegalArgumentException.class, () -> UserCookieModel.parse(value));
    }
}