package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class ActiveAuthorizationCodeFixture extends Fixture<ActiveAuthorizationCode> {
    private final ClientFixture clientFixture;

    public ActiveAuthorizationCodeFixture() {
        super();
        this.clientFixture = new ClientFixture();
    }


    public ActiveAuthorizationCode createRandomOne() {
        return Instancio.of(authorizationCodeModel(null))
                .create();
    }

    public ActiveAuthorizationCode createRandomOne(String url) {
        return Instancio.of(authorizationCodeModel(url))
                .create();
    }

    private Model<ActiveAuthorizationCode> authorizationCodeModel(String url) {
        return Instancio.of(ActiveAuthorizationCode.class)
                .supply(field(ActiveAuthorizationCode::getId), UUID::randomUUID)
                .supply(field(ActiveAuthorizationCode::getExpiresAt), () ->
                        OffsetDateTime.ofInstant(faker.date().future(5, TimeUnit.HOURS).toInstant(), ZoneOffset.UTC))
                .supply(field(ActiveAuthorizationCode::getRedirectUri), () -> url == null ? faker.internet().url() : url)
                .supply(field(ActiveAuthorizationCode::getUserId), () -> faker.random().nextLong())
                .supply(field(ActiveAuthorizationCode::getClient), () -> clientFixture.createRandomOne(false))
                .toModel();
    }
}
