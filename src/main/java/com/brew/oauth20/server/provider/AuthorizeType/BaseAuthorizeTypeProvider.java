package com.brew.oauth20.server.provider.AuthorizeType;

import com.brew.oauth20.server.model.ValidationResultModel;
import jakarta.servlet.http.HttpServletRequest;

public abstract class BaseAuthorizeTypeProvider implements IBaseAuthorizeTypeProvider {
    public abstract ValidationResultModel Validate(HttpServletRequest request);
}
