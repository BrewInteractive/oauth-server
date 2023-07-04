package com.brew.oauth20.server.utils.validators.constraints;


import com.brew.oauth20.server.utils.validators.TokenRequestModelValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TokenRequestModelValidator.class)
public @interface ValidateTokenRequestModel {
    String message() default "redirect_uri is required for grant_type authorization_code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}