package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.utils.validators.constraints.ValidateTokenRequestModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TokenRequestModelValidator implements ConstraintValidator<ValidateTokenRequestModel, TokenRequestModel> {

    @Override
    public boolean isValid(TokenRequestModel value, ConstraintValidatorContext context) {
        if (value != null && value.grant_type != null) {
            var grantType = GrantType.fromValue(value.grant_type);
            if (grantType != null && grantType.equals(GrantType.authorization_code)) {
                return value.getRedirect_uri() != null && !value.getRedirect_uri().isEmpty();
            }
            return true;
        }
        return false;
    }
}
