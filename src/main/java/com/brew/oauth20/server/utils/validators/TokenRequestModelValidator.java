package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.utils.validators.constraints.ValidateTokenRequestModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TokenRequestModelValidator implements ConstraintValidator<ValidateTokenRequestModel, TokenRequestModel> {

    @Override
    public boolean isValid(TokenRequestModel value, ConstraintValidatorContext context) {
        if (value != null) {
            var grantType = GrantType.fromValue(value.getGrantType());
            if (grantType != null && grantType.equals(GrantType.authorization_code)) {
                return value.getRedirectUri() != null && !value.getRedirectUri().isEmpty();
            }
            return true;
        }
        return false;
    }
}
