package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.ValidationResultModel;
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
    public ValidationResultModel validate(String authorizationHeader, TokenRequestModel tokenRequest) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(tokenRequest.getRefresh_token()))
            return new ValidationResultModel(false, "invalid_request");
        return super.validate(authorizationHeader, tokenRequest);
    }

    @Override
    public TokenResultModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest) {
        try {
            var validationResult = validate(authorizationHeader, tokenRequest);

            if (Boolean.FALSE.equals(validationResult.getResult()))
                return new TokenResultModel(null, validationResult.getError());

            var refreshToken = refreshTokenService.revokeRefreshToken(client.clientId(), tokenRequest.getRefresh_token(), client.refreshTokenExpiresInDays());

            var userId = refreshToken.getClientUser().getUserId();

            var accessToken = tokenService.generateToken(client, userId, tokenRequest.getState(), refreshToken.getScope(), tokenRequest.getAdditional_claims());

            var idToken = this.generateIdToken(accessToken, client, userId, tokenRequest.getState(), refreshToken.getScope(), tokenRequest.getAdditional_claims());

            var tokenModel = this.buildToken(accessToken, refreshToken.getToken(), idToken, tokenRequest.getState(), client.tokenExpiresInSeconds());

            return new TokenResultModel(tokenModel, null);
        } catch (ClientsUserNotFoundException e) {
            return new TokenResultModel(null, "unauthorized_client");
        }
    }
}