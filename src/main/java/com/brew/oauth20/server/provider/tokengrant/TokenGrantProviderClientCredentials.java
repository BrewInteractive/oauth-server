package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.ClientCredentialsModel;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.model.TokenRequestModel;
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
    public TokenModel generateToken(ClientCredentialsModel clientCredentials, TokenRequestModel tokenRequest) {
        super.validate(clientCredentials, tokenRequest);
        var accessToken = tokenService.generateToken(client, tokenRequest.getAdditionalClaims());
        return this.buildToken(accessToken, tokenRequest.getState(), client.tokenExpiresInSeconds());
    }
}
