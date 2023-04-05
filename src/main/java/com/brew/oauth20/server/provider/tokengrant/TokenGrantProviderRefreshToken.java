package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderRefreshToken extends BaseTokenGrantProvider {
    protected TokenGrantProviderRefreshToken(ClientService clientService) {
        super(clientService);
        grantType = GrantType.refresh_token;
    }

    @Override
    public TokenModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest) {
        return null;
    }
}