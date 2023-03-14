package com.brew.oauth20.server.provider.authorizeType;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.RedirectUriModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.utils.ClientValidator;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class BaseAuthorizeTypeProvider implements IBaseAuthorizeTypeProvider {
    protected ResponseType responseType;

    ClientService clientService;

    protected BaseAuthorizeTypeProvider(ClientService clientService) {
        this.clientService = clientService;
    }

    public ValidationResultModel Validate(UUID clientId, String redirectUri) {

        try {
            var client = clientService.getClient(clientId);

            var clientValidator = new ClientValidator(responseType.getResponseType(), redirectUri);

            //TODO: should mapper handle mapping operation
            var grantList = new ArrayList<>(client.getClientsGrants().stream().map(x -> new GrantModel(x.getGrant().getId(), x.getGrant().getResponseType())).collect(Collectors.toList()));
            var redirectUrlList = new ArrayList<>(client.getRedirectUrises().stream().map(x -> new RedirectUriModel(x.getId(), x.getRedirectUri())).collect(Collectors.toList()));
            return clientValidator.validate(
                    new ClientModel(
                            client.getId(),
                            grantList,
                            redirectUrlList
                    )
            );
        } catch (ClientNotFoundException e) {
            return new ValidationResultModel(false, "unauthorized_client");
        }
    }
}
