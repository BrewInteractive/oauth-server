package com.brew.oauth20.server.provider.AuthorizeType.Code;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.provider.AuthorizeType.BaseAuthorizeTypeProvider;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeTypeProviderAuthorizationCode extends BaseAuthorizeTypeProvider {
    public AuthorizeTypeProviderAuthorizationCode(ClientService clientService) {
        super(clientService);
        responseType = ResponseType.code;
    }
}
