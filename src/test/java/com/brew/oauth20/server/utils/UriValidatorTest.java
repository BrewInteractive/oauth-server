package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.utils.validators.UriValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UriValidatorTest {

    @Test
    void valid_uri_should_return_true() {
        // Arrange
        UriValidator uriValidator = new UriValidator();
        String validUri = "https://www.example.com";
        ConstraintValidatorContext context = null;

        // Act
        boolean isValid = uriValidator.isValid(validUri, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void invalid_uri_should_return_false() {
        // Arrange
        UriValidator uriValidator = new UriValidator();
        String invalidUri = "not_a_valid_uri";
        ConstraintValidatorContext context = null;

        // Act
        boolean isValid = uriValidator.isValid(invalidUri, context);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void null_uri_should_return_false() {
        // Arrange
        UriValidator uriValidator = new UriValidator();
        String nullUri = null;
        ConstraintValidatorContext context = null;

        // Act
        boolean isValid = uriValidator.isValid(nullUri, context);

        // Assert
        assertFalse(isValid);
    }
}