package com.brew.oauth20.server.provider.authorizeType;

import com.brew.oauth20.server.model.ValidationResultModel;

import java.util.UUID;

public interface IBaseAuthorizeTypeProvider {
    ValidationResultModel Validate(UUID clientId, String redirectUri);
}
