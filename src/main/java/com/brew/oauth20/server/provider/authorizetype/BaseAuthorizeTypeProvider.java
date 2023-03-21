package com.brew.oauth20.server.provider.authorizetype;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.utils.ClientValidator;

import java.util.UUID;

public abstract class BaseAuthorizeTypeProvider {
    protected ResponseType responseType;

    ClientService clientService;

    protected BaseAuthorizeTypeProvider(ClientService clientService) {
        this.clientService = clientService;
    }

    public ValidationResultModel validate(UUID clientId, String redirectUri) {
        var clientModel = clientService.getClient(clientId);

        if (clientModel == null)
            return new ValidationResultModel(false, "unauthorized_client");

        var clientValidator = new ClientValidator(responseType.getResponseType(), redirectUri);

        return clientValidator.validate(clientModel);
    }
}
