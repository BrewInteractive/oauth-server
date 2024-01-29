package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.enums.Scope;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import com.brew.oauth20.server.testUtils.ScopeUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class ActiveAuthorizationCodeFixture extends Fixture<ActiveAuthorizationCode> {
    private final ClientUserFixture clientUserFixture;

    public ActiveAuthorizationCodeFixture() {
        super();
        this.clientUserFixture = new ClientUserFixture();
    }


    public ActiveAuthorizationCode createRandomOne() {
        return Instancio.of(authorizationCodeModel(null, null))
                .create();
    }

    public ActiveAuthorizationCode createRandomOne(String url) {
        return Instancio.of(authorizationCodeModel(null, url))
                .create();
    }

    public ActiveAuthorizationCode createRandomOne(ClientUser clientUser, String url) {
        return Instancio.of(authorizationCodeModel(clientUser, url))
                .create();
    }

    private Model<ActiveAuthorizationCode> authorizationCodeModel(ClientUser clientUser, String url) {
        return Instancio.of(ActiveAuthorizationCode.class)
                .supply(field(ActiveAuthorizationCode::getId), UUID::randomUUID)
                .supply(field(ActiveAuthorizationCode::getExpiresAt), () ->
                        OffsetDateTime.ofInstant(faker.date().future(5, TimeUnit.HOURS).toInstant(), ZoneOffset.UTC))
                .supply(field(ActiveAuthorizationCode::getRedirectUri), () -> url != null ? url : faker.internet().url())
                .supply(field(ActiveAuthorizationCode::getClientUser), () -> clientUser != null ? clientUser : clientUserFixture.createRandomOne())
                .supply(field(ActiveAuthorizationCode::getScope), () -> ScopeUtils.createScopeString(FakerUtils.createRandomScopeList(faker, Scope.values())))
                .toModel();
    }


}
