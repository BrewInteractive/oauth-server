package com.brew.oauth20.server.provider.authorizetype;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.utils.validators.ClientValidator;

import java.util.Optional;

public abstract class BaseAuthorizeTypeProvider {
    protected ResponseType responseType;

    ClientService clientService;

    protected BaseAuthorizeTypeProvider(ClientService clientService) {
        this.clientService = clientService;
    }

    public ValidationResultModel validate(String clientId, String redirectUri) {
        var clientModel = clientService.getClient(clientId);

        if (clientModel == null)
            return new ValidationResultModel(false, "unauthorized_client");

        return new ClientValidator(clientModel).validate(responseType.getResponseType(), redirectUri, Optional.ofNullable(null));
    }
}
