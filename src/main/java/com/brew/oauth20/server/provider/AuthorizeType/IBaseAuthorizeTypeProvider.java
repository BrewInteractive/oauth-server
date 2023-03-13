package com.brew.oauth20.server.provider.AuthorizeType;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.util.Pair;

public interface IBaseAuthorizeTypeProvider {
    Pair<Boolean, String> Validate(HttpServletRequest request);
}
