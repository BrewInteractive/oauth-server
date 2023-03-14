package com.brew.oauth20.server.provider.AuthorizeType;

import com.brew.oauth20.server.model.ValidationResultModel;

public interface IBaseAuthorizeTypeProvider {
    ValidationResultModel Validate(String clientId, String redirectUri);
}
