package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderRefreshToken extends BaseTokenGrantProvider {
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    TokenService tokenService;


    protected TokenGrantProviderRefreshToken(
            ) {
        super();
        this.grantType = GrantType.refresh_token;
    }

    @Override
    public ValidationResultModel validate(String authorizationHeader, TokenRequestModel tokenRequest) {
        if (tokenRequest.refresh_token.isBlank())
            return new ValidationResultModel(false, "invalid_request");
        var validationResult = super.validate(authorizationHeader, tokenRequest);
        if (Boolean.FALSE.equals(validationResult.getResult()))
            return validationResult;
        return new ValidationResultModel(true, null);
    }

    @Override
    public TokenResultModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest) {
        var validationResult = validate(authorizationHeader, tokenRequest);

        if (Boolean.FALSE.equals(validationResult.getResult()))
            return new TokenResultModel(null, validationResult.getError());

        var newRefreshTokenCode = StringUtils.generateSecureRandomString(54);

        var refreshToken = refreshTokenService.revokeRefreshToken(client.clientId(), tokenRequest.refresh_token, client.refreshTokenExpiresInDays(), newRefreshTokenCode);

        var userId = refreshToken.getClientUser().getUserId();

        var tokenModel = tokenService.generateToken(client, userId, tokenRequest.state, refreshToken.getToken());

        return new TokenResultModel(tokenModel, null);
    }
}