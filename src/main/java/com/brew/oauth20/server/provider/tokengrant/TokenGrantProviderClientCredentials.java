package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderClientCredentials extends BaseTokenGrantProvider {
    @Autowired
    TokenService tokenService;

    protected TokenGrantProviderClientCredentials() {
        super();
        this.grantType = GrantType.client_credentials;
    }

    @Override
    public ValidationResultModel validate(String authorizationHeader, TokenRequestModel tokenRequest) {
        if (StringUtils.isEmpty(tokenRequest.client_id) || StringUtils.isEmpty(tokenRequest.client_secret))
            return new ValidationResultModel(false, "invalid_request");
        return super.validate(authorizationHeader, tokenRequest);
    }

    @Override
    public TokenResultModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest) {
        var validationResult = validate(authorizationHeader, tokenRequest);

        if (Boolean.FALSE.equals(validationResult.getResult()))
            return new TokenResultModel(null, validationResult.getError());

        var tokenModel = tokenService.generateToken(client, null, tokenRequest.state);

        return new TokenResultModel(tokenModel, null);
    }
}