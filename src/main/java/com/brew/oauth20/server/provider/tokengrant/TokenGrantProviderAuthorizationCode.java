package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientAuthenticationFailedException;
import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.model.ClientCredentialsModel;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderAuthorizationCode extends BaseTokenGrantProvider {

    private final AuthorizationCodeService authorizationCodeService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    protected TokenGrantProviderAuthorizationCode(ClientService clientService,
                                                  TokenService tokenService,
                                                  UserIdentityService userIdentityService,
                                                  Environment env,
                                                  AuthorizationCodeService authorizationCodeService,
                                                  RefreshTokenService refreshTokenService) {
        super(clientService, tokenService, userIdentityService, env);
        this.authorizationCodeService = authorizationCodeService;
        this.refreshTokenService = refreshTokenService;
        this.grantType = GrantType.authorization_code;
    }

    @Override
    public Boolean validate(ClientCredentialsModel clientCredentials, TokenRequestModel tokenRequest) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(tokenRequest.getCode()))
            throw new OAuthException(OAuthError.INVALID_REQUEST);
        return super.validate(clientCredentials, tokenRequest);
    }

    @Override
    public TokenModel generateToken(ClientCredentialsModel clientCredentials, TokenRequestModel tokenRequest) {
        validate(clientCredentials, tokenRequest);

        var activeAuthorizationCode = this.authorizationCodeService.getAuthorizationCode(
                tokenRequest.getCode(),
                tokenRequest.getRedirectUri(),
                true);

        if (activeAuthorizationCode == null)
            throw new ClientAuthenticationFailedException();

        var userId = activeAuthorizationCode.getClientUser().getUserId();

        String refreshToken = null;
        if (Boolean.TRUE.equals(client.issueRefreshTokens())) {
            var refreshTokenEntity = this.refreshTokenService.createRefreshToken(activeAuthorizationCode.getClientUser(), client.refreshTokenExpiresInDays());
            refreshToken = refreshTokenEntity.getToken();
        }
        var accessToken = this.tokenService.generateToken(client, userId, activeAuthorizationCode.getScope(), tokenRequest.getAdditionalClaims());

        var idToken = this.generateIdToken(accessToken, client, userId, activeAuthorizationCode.getScope(), tokenRequest.getAdditionalClaims());

        return this.buildToken(accessToken, refreshToken, idToken, tokenRequest.getState(), client.tokenExpiresInSeconds());
    }
}