package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Grant;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.LinkedHashSet;

import static org.instancio.Select.field;

public class GrantFixture extends Fixture<Grant> {
    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.code, ResponseType.token};
    private final GrantType[] defaultGrantTypeOptions = new GrantType[]{GrantType.client_credentials, GrantType.refresh_token, GrantType.authorization_code};

    public Grant createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public Grant createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(grant(responseTypeOptions, defaultGrantTypeOptions))
                .create();
    }

    public Grant createRandomOne(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions) {
        return Instancio.of(grant(responseTypeOptions, grantTypeOptions))
                .create();
    }

    private Model<Grant> grant(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions) {
        return Instancio.of(Grant.class)
                .supply(field(Grant::getClientGrants), () -> new LinkedHashSet<>())
                .supply(field(Grant::getResponseType), () -> FakerUtils.createRandomResponseType(faker, responseTypeOptions))
                .supply(field(Grant::getGrantType), () -> FakerUtils.createRandomGrantType(faker, grantTypeOptions))
                .toModel();
    }
}
