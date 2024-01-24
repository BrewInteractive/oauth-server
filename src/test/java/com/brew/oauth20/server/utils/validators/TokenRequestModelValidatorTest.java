package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.model.TokenRequestModel;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class TokenRequestModelValidatorTest {
    @Test
    void test_valid_authorization_code() {
        // Arrange
        TokenRequestModelValidator validator = new TokenRequestModelValidator();
        TokenRequestModel validModel = TokenRequestModel.builder()
                .grant_type("authorization_code")
                .redirect_uri("https://example.com/callback")
                .build();

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // Act
        boolean isValid = validator.isValid(validModel, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void test_invalid_authorization_code() {
        // Arrange
        TokenRequestModelValidator validator = new TokenRequestModelValidator();
        TokenRequestModel invalidModel = TokenRequestModel.builder()
                .grant_type("authorization_code")
                .redirect_uri("")
                .build();

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // Act
        boolean isValid = validator.isValid(invalidModel, context);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void test_null_value() {
        // Arrange
        TokenRequestModelValidator validator = new TokenRequestModelValidator();
        TokenRequestModel nullModel = null;

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // Act
        boolean isValid = validator.isValid(nullModel, context);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void test_non_authorization_code_grant_type() {
        // Arrange
        TokenRequestModelValidator validator = new TokenRequestModelValidator();
        TokenRequestModel model = TokenRequestModel.builder()
                .grant_type("client_credentials")
                .redirect_uri("https://example.com/callback")
                .build();

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        // Act
        boolean isValid = validator.isValid(model, context);

        // Assert
        assertTrue(isValid);
    }
}