package com.brew.oauth20.server.provider.AuthorizeType.Token;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.provider.AuthorizeType.BaseAuthorizeTypeProvider;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeTypeProviderToken extends BaseAuthorizeTypeProvider {
    public AuthorizeTypeProviderToken(ClientService clientService) {
        super(clientService);
        responseType = ResponseType.code;
    }
}
