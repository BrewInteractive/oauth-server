package com.brew.oauth20.server.controller.base;

import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.model.enums.OAuthError;
import org.springframework.validation.BindingResult;

public abstract class BaseController {
    protected void validateRequest(BindingResult validationResult) {
        if (validationResult.hasErrors())
            throw new OAuthException(OAuthError.INVALID_REQUEST);
    }
}
