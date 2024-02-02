package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.service.UserIdentityService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderClientCredentials extends BaseTokenGrantProvider {
    protected TokenGrantProviderClientCredentials(ClientService clientService,
                                                  TokenService tokenService,
                                                  UserIdentityService userIdentityService,
                                                  Environment env) {
        super(clientService, tokenService, userIdentityService, env);
        this.grantType = GrantType.client_credentials;
    }

    @Override
    public TokenResultModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest) {
        try {
            var validationResult = super.validate(authorizationHeader, tokenRequest);

            if (Boolean.FALSE.equals(validationResult.getResult()))
                return new TokenResultModel(null, validationResult.getError());

            var accessToken = tokenService.generateToken(client, tokenRequest.getState(), tokenRequest.getAdditional_claims());

            var tokenModel = this.buildToken(accessToken, tokenRequest.getState(), client.tokenExpiresInSeconds());

            return new TokenResultModel(tokenModel, null);
        } catch (ClientsUserNotFoundException e) {
            return new TokenResultModel(null, "unauthorized_client");
        }
    }
}
