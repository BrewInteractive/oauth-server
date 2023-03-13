package com.brew.oauth20.server.provider.AuthorizeType;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.util.Pair;


public abstract class BaseAuthorizeTypeProvider implements IBaseAuthorizeTypeProvider {
    public abstract Pair<Boolean, String> Validate(HttpServletRequest request);
}

