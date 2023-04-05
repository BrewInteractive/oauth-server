package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.utils.validators.ClientValidator;

public abstract class BaseTokenGrantProvider {
    ClientService clientService;

    protected BaseTokenGrantProvider(ClientService clientService) {
        this.clientService = clientService;
    }

    public ValidationResultModel validate(String authorizationHeader, TokenRequestModel tokenRequest) {
        var clientCredentials = clientService.decodeClientCredentials(authorizationHeader);

        if (clientCredentials.isEmpty())
            return new ValidationResultModel(false, "invalid_request");

        var client = clientService.getClient(clientCredentials.get().getFirst());

        if (client == null)
            return new ValidationResultModel(false, "unauthorized_client");

        ClientValidator clientValidator = new ClientValidator(client);

        return clientValidator.validate(tokenRequest.grant_type);
    }

    public abstract TokenModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest);
}
