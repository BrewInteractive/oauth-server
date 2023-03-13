package com.brew.oauth20.server.provider.AuthorizeType.Code;

import com.brew.oauth20.server.provider.AuthorizeType.BaseAuthorizeTypeProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeTypeProviderCode extends BaseAuthorizeTypeProvider {
    public Pair<Boolean, String> Validate(HttpServletRequest request) {
        return Pair.of(true, "AuthorizeTypeProviderCode");
    }
}

