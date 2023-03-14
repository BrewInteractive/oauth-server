package com.brew.oauth20.server.provider.AuthorizeType;


import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.provider.AuthorizeType.Code.AuthorizeTypeProviderCode;
import com.brew.oauth20.server.provider.AuthorizeType.Token.AuthorizeTypeProviderToken;
import com.brew.oauth20.server.service.factory.ServiceFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class AuthorizeTypeProviderFactory extends ServiceFactory<ResponseType, BaseAuthorizeTypeProvider> {
    public AuthorizeTypeProviderFactory() {

        Map<ResponseType, Type> dict = Map.of(
                ResponseType.code, AuthorizeTypeProviderCode.class,
                ResponseType.token, AuthorizeTypeProviderToken.class
        );

        setRegisteredServiceTypes(dict);
    }
}
