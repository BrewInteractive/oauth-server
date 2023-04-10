package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.utils.validators.ClientValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseTokenGrantProvider {
    protected GrantType grantType;
    protected ClientModel client;
    @Autowired
    ClientService clientService;

    public ValidationResultModel validate(String authorizationHeader, TokenRequestModel tokenRequest) {
        String clientId;
        String clientSecret;
        if (StringUtils.isEmpty(authorizationHeader)) {
            clientId = tokenRequest.client_id;
            clientSecret = tokenRequest.client_secret;
        } else {
            var clientCredentials = clientService.decodeClientCredentials(authorizationHeader);
            if (clientCredentials.isEmpty())
                return new ValidationResultModel(false, "unauthorized_client");
            clientId = clientCredentials.get().getFirst();
            clientSecret = clientCredentials.get().getSecond();
        }

        client = clientService.getClient(clientId, clientSecret);

        if (client == null)
            return new ValidationResultModel(false, "unauthorized_client");

        ClientValidator clientValidator = new ClientValidator(client);

        return clientValidator.validate(tokenRequest.grant_type);
    }

    public abstract TokenResultModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest);
}