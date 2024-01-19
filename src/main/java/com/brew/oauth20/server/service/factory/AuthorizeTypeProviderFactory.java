package com.brew.oauth20.server.service.factory;


import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderAuthorizationCode;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderToken;
import com.brew.oauth20.server.provider.authorizetype.BaseAuthorizeTypeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class AuthorizeTypeProviderFactory extends ServiceFactory<ResponseType, BaseAuthorizeTypeProvider> {
    @Autowired
    public AuthorizeTypeProviderFactory(ApplicationContext context) {
        super(context);
        Map<ResponseType, Type> map = Map.of(
                ResponseType.code, AuthorizeTypeProviderAuthorizationCode.class,
                ResponseType.token, AuthorizeTypeProviderToken.class
        );

        setRegisteredServiceTypes(map);
    }
}
