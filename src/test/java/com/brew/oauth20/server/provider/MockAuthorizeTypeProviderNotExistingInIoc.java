package com.brew.oauth20.server.provider;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.provider.AuthorizeType.BaseAuthorizeTypeProvider;

public class MockAuthorizeTypeProviderNotExistingInIoc extends BaseAuthorizeTypeProvider {
    @Override
    public String getResponseType() {
        return ResponseType.code.getResponseType();
    }

    @Override
    public ValidationResultModel Validate(String clientId, String redirectUri) {
        return new ValidationResultModel(true, "AuthorizeTypeProviderAuthorizationCode");
    }
}
