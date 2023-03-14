package com.brew.oauth20.server.provider.AuthorizeType;

import com.brew.oauth20.server.model.ValidationResultModel;
import jakarta.servlet.http.HttpServletRequest;

public interface IBaseAuthorizeTypeProvider {
    ValidationResultModel Validate(HttpServletRequest request);
}
