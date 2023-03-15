package com.brew.oauth20.server.provider.authorizetype;


import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.service.factory.ServiceFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class AuthorizeTypeProviderFactory extends ServiceFactory<ResponseType, BaseAuthorizeTypeProvider> {
    public AuthorizeTypeProviderFactory() {

        Map<ResponseType, Type> map = Map.of(
                ResponseType.CODE, AuthorizeTypeProviderAuthorizationCode.class,
                ResponseType.TOKEN, AuthorizeTypeProviderToken.class
        );

        setRegisteredServiceTypes(map);
    }
}