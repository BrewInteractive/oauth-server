package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.ValidationResultModel;
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
    public ValidationResultModel validate(String authorizationHeader, TokenRequestModel tokenRequest) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(tokenRequest.code))
            return new ValidationResultModel(false, "invalid_request");
        return super.validate(authorizationHeader, tokenRequest);
    }

    @Override
    public TokenResultModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest) {
        try {
            var validationResult = validate(authorizationHeader, tokenRequest);

            if (Boolean.FALSE.equals(validationResult.getResult()))
                return new TokenResultModel(null, validationResult.getError());

            var activeAuthorizationCode = this.authorizationCodeService.getAuthorizationCode(tokenRequest.code, tokenRequest.redirect_uri, true);

            if (activeAuthorizationCode == null)
                return new TokenResultModel(null, "invalid_request");

            var userId = activeAuthorizationCode.getClientUser().getUserId();

            String refreshToken = null;
            if (Boolean.TRUE.equals(client.issueRefreshTokens())) {
                var refreshTokenEntity = this.refreshTokenService.createRefreshToken(client.clientId(), userId, client.refreshTokenExpiresInDays());
                refreshToken = refreshTokenEntity.getToken();
            }
            var accessToken = this.tokenService.generateToken(client, userId, tokenRequest.getState(), activeAuthorizationCode.getScope(), tokenRequest.getAdditional_claims());

            var idToken = this.generateIdToken(accessToken, client, userId, tokenRequest.getState(), activeAuthorizationCode.getScope(), tokenRequest.getAdditional_claims());

            var tokenModel = this.buildToken(accessToken, refreshToken, idToken, tokenRequest.getState(), client.tokenExpiresInSeconds());

            return new TokenResultModel(tokenModel, null);
        } catch (ClientsUserNotFoundException e) {
            return new TokenResultModel(null, "unauthorized_client");
        }
    }
}