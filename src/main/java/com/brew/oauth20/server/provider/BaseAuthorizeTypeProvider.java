package com.brew.oauth20.server.provider;

import com.brew.oauth20.server.model.ValidationResultModel;

public abstract class BaseAuthorizeTypeProvider {
    public abstract String getResponseType();

    public ValidationResultModel validate(String clientId, String redirectUri) {

    }
}
