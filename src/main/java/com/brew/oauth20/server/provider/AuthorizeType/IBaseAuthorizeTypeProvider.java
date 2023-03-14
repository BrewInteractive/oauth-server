package com.brew.oauth20.server.provider.AuthorizeType;

import java.util.UUID;

import com.brew.oauth20.server.model.ValidationResultModel;

public interface IBaseAuthorizeTypeProvider {
    ValidationResultModel Validate(UUID clientId, String redirectUri);
}
