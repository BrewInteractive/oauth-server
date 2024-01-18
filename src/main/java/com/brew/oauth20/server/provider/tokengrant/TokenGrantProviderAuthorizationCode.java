package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderAuthorizationCode extends BaseTokenGrantProvider {

    private final AuthorizationCodeService authorizationCodeService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    protected TokenGrantProviderAuthorizationCode(ClientService clientService,
                                                  TokenService tokenService,
                                                  AuthorizationCodeService authorizationCodeService,
                                                  RefreshTokenService refreshTokenService) {
        super(clientService, tokenService);
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

            // todo: getUserIdentity on base class then send to generate token generate id token as well. (TMID-931)

            var tokenModel = this.tokenService.generateToken(client, userId, tokenRequest.state, tokenRequest.getAdditional_claims(), refreshToken);

            return new TokenResultModel(tokenModel, null);
        } catch (ClientsUserNotFoundException e) {
            return new TokenResultModel(null, "unauthorized_client");
        }
    }
}