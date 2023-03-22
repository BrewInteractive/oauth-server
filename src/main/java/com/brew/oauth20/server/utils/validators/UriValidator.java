package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.utils.UriUtils;
import com.brew.oauth20.server.utils.validators.constraints.UriConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.MalformedURLException;

public class UriValidator implements ConstraintValidator<UriConstraint, String> {
    public boolean isValid(String valueToValidate, final ConstraintValidatorContext context) {
        try {
            return UriUtils.isValidUrl(valueToValidate);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
