package com.brew.oauth20.server.provider.AuthorizeType.Token;

import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.provider.AuthorizeType.BaseAuthorizeTypeProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeTypeProviderToken extends BaseAuthorizeTypeProvider {
    public ValidationResultModel Validate(HttpServletRequest request) {
        return new ValidationResultModel(true, "AuthorizeTypeProviderToken");
    }
}
