package com.brew.oauth20.server.utils;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StringUtilsTest {

    private static Stream<Arguments> generate_secure_random_string_should_return_random_results() {
        return Stream.of(
                Arguments.of(0, 32, "", "[0-9a-zA-Z-_]+"),
                Arguments.of(123, 123, "", "[0-9a-zA-Z-_]+"),
                Arguments.of(0, 32, "abc", "[abc]+"),
                Arguments.of(321, 321, "abc", "[abc]+")
        );
    }

    @MethodSource
    @ParameterizedTest
    void generate_secure_random_string_should_return_random_results(int length, int exptectedLength, String chars, String regexSchema) {
        String randomString1;

        if (length == 0 && chars.isEmpty())
            randomString1 = StringUtils.generateSecureRandomString();
        else if (length != 0 && chars.isEmpty())
            randomString1 = StringUtils.generateSecureRandomString(length);
        else if (length == 0)
            randomString1 = StringUtils.generateSecureRandomString(chars);
        else
            randomString1 = StringUtils.generateSecureRandomString(length, chars);

        assertNotEquals("", randomString1);
        assertEquals(exptectedLength, randomString1.length());
        assertTrue(randomString1.matches(regexSchema));
    }
}