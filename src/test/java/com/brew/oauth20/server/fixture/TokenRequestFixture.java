package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import com.brew.oauth20.server.utils.StringUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class TokenRequestFixture extends Fixture<TokenRequestModel> {

    private final GrantType[] defaultGrantTypeOptions = new GrantType[]{GrantType.refresh_token,
            GrantType.authorization_code, GrantType.client_credentials};

    public TokenRequestFixture() {
        super();
    }

    public TokenRequestModel createRandomOne() {
        return createRandomOne(this.defaultGrantTypeOptions);
    }

    public TokenRequestModel createRandomOne(GrantType[] grantTypeOptions) {
        return Instancio.of(tokenRequest(grantTypeOptions))
                .create();
    }

    private Model<TokenRequestModel> tokenRequest(GrantType[] grantTypeOptions) {
        return Instancio.of(TokenRequestModel.class)
                .supply(field(TokenRequestModel::getRefresh_token), () -> StringUtils.generateSecureRandomString())
                .supply(field(TokenRequestModel::getCode), () -> StringUtils.generateSecureRandomString())
                .supply(field(TokenRequestModel::getState), () -> faker.rickAndMorty().quote())
                .supply(field(TokenRequestModel::getClient_id), () -> StringUtils.generateSecureRandomString())
                .supply(field(TokenRequestModel::getClient_secret), () -> StringUtils.generateSecureRandomString())
                .supply(field(TokenRequestModel::getGrant_type), () -> FakerUtils.createRandomGrantType(faker, grantTypeOptions).getGrantType())
                .supply(field(TokenRequestModel::getRedirect_uri), () -> faker.internet().url())
                .toModel();
    }
}
