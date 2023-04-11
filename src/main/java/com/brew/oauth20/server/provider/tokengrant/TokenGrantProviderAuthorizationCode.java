package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenGrantProviderAuthorizationCode extends BaseTokenGrantProvider {
    @Autowired
    AuthorizationCodeService authorizationCodeService;
    @Autowired
    TokenService tokenService;

    protected TokenGrantProviderAuthorizationCode() {
        super();
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
        var validationResult = validate(authorizationHeader, tokenRequest);

        if (Boolean.FALSE.equals(validationResult.getResult()))
            return new TokenResultModel(null, validationResult.getError());

        var activeAuthorizationCode = authorizationCodeService.getAuthorizationCode(tokenRequest.code, tokenRequest.redirect_uri, true);

        if (activeAuthorizationCode == null)
            return new TokenResultModel(null, "invalid_request");

        var tokenModel = tokenService.generateToken(client, activeAuthorizationCode.getUserId(), tokenRequest.state);

        return new TokenResultModel(tokenModel, null);
    }
}