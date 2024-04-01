package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.model.ClientCredentialsModel;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.service.UserIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderRefreshToken extends BaseTokenGrantProvider {

    private final RefreshTokenService refreshTokenService;

    @Autowired
    protected TokenGrantProviderRefreshToken(ClientService clientService,
                                             TokenService tokenService,
                                             UserIdentityService userIdentityService,
                                             Environment env,
                                             RefreshTokenService refreshTokenService) {
        super(clientService, tokenService, userIdentityService, env);
        this.refreshTokenService = refreshTokenService;
        this.grantType = GrantType.refresh_token;
    }

    @Override
    public Boolean validate(ClientCredentialsModel clientCredentials, TokenRequestModel tokenRequest) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(tokenRequest.getRefreshToken()))
            throw new OAuthException(OAuthError.INVALID_REQUEST);
        return super.validate(clientCredentials, tokenRequest);
    }

    @Override
    public TokenModel generateToken(ClientCredentialsModel clientCredentials, TokenRequestModel tokenRequest) {
        validate(clientCredentials, tokenRequest);

        var refreshToken = refreshTokenService.revokeRefreshToken(client.clientId(), tokenRequest.getRefreshToken(), client.refreshTokenExpiresInDays());

        var userId = refreshToken.getClientUser().getUserId();

        var accessToken = tokenService.generateToken(client, userId, refreshToken.getScope(), tokenRequest.getAdditionalClaims());

        var idToken = this.generateIdToken(accessToken, client, userId, refreshToken.getScope(), tokenRequest.getAdditionalClaims());

        return this.buildToken(accessToken, refreshToken.getToken(), idToken, tokenRequest.getState(), client.tokenExpiresInSeconds());

    }
}