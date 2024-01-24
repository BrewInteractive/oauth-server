package com.brew.oauth20.server.utils.validators;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ALL")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScopeValidatorTest {
    private static Faker faker;

    public ScopeValidatorTest() {
        faker = new Faker();
    }

    private static Stream<Arguments> validate_scope_successful() {
        return Stream.of(
                Arguments.of(
                        "email",
                        new String[]{"email"}
                ),
                Arguments.of(
                        "email",
                        new String[]{"email", "profile", "openid"}
                ),
                Arguments.of(
                        "email profile openid",
                        new String[]{"email", "profile", "openid"}
                ),
                Arguments.of(
                        "",
                        new String[]{"email", "profile", "openid"}
                ), // "model" is an empty string.
                Arguments.of(
                        "     ",
                        new String[]{"email", "profile", "openid"}
                ) // "model" consists only of spaces.
        );
    }

    private static Stream<Arguments> validate_scope_failure() {
        return Stream.of(
                Arguments.of(
                        "profile",
                        new String[]{"email"}
                ),
                Arguments.of(
                        "email",
                        new String[]{"openid", "profile"}
                ),
                Arguments.of(
                        faker.letterify("?".repeat(7)),
                        new String[]{"email", "profile", "openid"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void validate_scope_successful(String requiredScopes, String[] authorizedScopes) {
        // Act
        var scopeValidator = new ScopeValidator(requiredScopes);
        var actualResult = scopeValidator.validateScope(authorizedScopes);

        // Assert
        assertTrue(actualResult);
    }

    @ParameterizedTest
    @MethodSource
    void validate_scope_failure(String requiredScopes, String[] authorizedScopes) {
        // Act
        var scopeValidator = new ScopeValidator(requiredScopes);
        var actualResult = scopeValidator.validateScope(authorizedScopes);

        // Assert
        assertFalse(actualResult);
    }
}
