package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class TokenRequestModelFixture extends Fixture<TokenRequestModel> {

    private final GrantType[] defaultGrantTypeOptions = new GrantType[]{GrantType.refresh_token,
            GrantType.authorization_code, GrantType.client_credentials};

    public TokenRequestModelFixture() {
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
                .supply(field(TokenRequestModel::getRefresh_token), () -> faker.letterify("*********************"))
                .supply(field(TokenRequestModel::getCode), () -> faker.letterify("*********************"))
                .supply(field(TokenRequestModel::getState), () -> faker.rickAndMorty().quote())
                .supply(field(TokenRequestModel::getClient_id), () -> faker.letterify("*********************"))
                .supply(field(TokenRequestModel::getClient_secret), () -> faker.letterify("*********************"))
                .supply(field(TokenRequestModel::getGrant_type), () -> FakerUtils.createRandomGrantType(faker, grantTypeOptions).getGrantType())
                .supply(field(TokenRequestModel::getRedirect_uri), () -> faker.internet().url())
                .toModel();
    }
}
