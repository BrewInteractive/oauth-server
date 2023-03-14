package com.brew.oauth20.server.provider.AuthorizeType;

import com.brew.oauth20.server.model.ValidationResultModel;

public abstract class BaseAuthorizeTypeProvider implements IBaseAuthorizeTypeProvider {
    public abstract String getResponseType();

    public abstract ValidationResultModel Validate(String clientId, String redirectUri);
}
