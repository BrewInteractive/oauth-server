package com.brew.oauth20.server.provider.authorizetype;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.utils.validators.ClientValidator;

public abstract class BaseAuthorizeTypeProvider {
    protected ResponseType responseType;

    ClientService clientService;

    protected BaseAuthorizeTypeProvider(ClientService clientService) {
        this.clientService = clientService;
    }

    public Boolean validate(String clientId, String redirectUri, String scope) {
        var clientModel = clientService.getClient(clientId);

        if (clientModel == null)
            throw new OAuthException(OAuthError.UNAUTHORIZED_CLIENT);

        return new ClientValidator(clientModel).validate(responseType.getResponseType(), redirectUri, scope);
    }
}
