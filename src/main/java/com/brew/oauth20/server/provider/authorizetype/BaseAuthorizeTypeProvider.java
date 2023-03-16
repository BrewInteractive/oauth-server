package com.brew.oauth20.server.provider.authorizetype;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.RedirectUriModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.utils.ClientValidator;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class BaseAuthorizeTypeProvider {
    protected ResponseType responseType;

    ClientService clientService;

    protected BaseAuthorizeTypeProvider(ClientService clientService) {
        this.clientService = clientService;
    }

    public ValidationResultModel validate(UUID clientId, String redirectUri) {
        var optionalClient = clientService.getClient(clientId);
        if (optionalClient.isEmpty())
            return new ValidationResultModel(false, "unauthorized_client");

        var client = optionalClient.get();
        var clientValidator = new ClientValidator(responseType.getResponseType(), redirectUri);

        //TODO: should mapper handle mapping operation
        var grantList = client.getClientsGrants().stream().map(x -> new GrantModel(x.getGrant().getId(), x.getGrant().getResponseType())).collect(Collectors.toCollection(ArrayList::new));
        var redirectUrlList = client.getRedirectUris().stream().map(x -> new RedirectUriModel(x.getId(), x.getRedirectUri())).collect(Collectors.toCollection(ArrayList::new));
        return clientValidator.validate(
                new ClientModel(
                        client.getId(),
                        grantList,
                        redirectUrlList
                )
        );
    }
}
