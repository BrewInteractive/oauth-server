package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.AuthorizationCode;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class AuthorizationCodeFixture extends Fixture<AuthorizationCode> {
    private final ClientFixture clientFixture;

    public AuthorizationCodeFixture() {
        super();
        this.clientFixture = new ClientFixture();
    }


    public AuthorizationCode createRandomOne() {
        return Instancio.of(authorizationCodeModel())
                .create();
    }

    public AuthorizationCode createRandomOne(String url) {
        return Instancio.of(authorizationCodeModel(url))
                .create();
    }

    private Model<AuthorizationCode> authorizationCodeModel() {
        return authorizationCodeModel(faker.internet().url());
    }

    private Model<AuthorizationCode> authorizationCodeModel(String url) {
        return Instancio.of(AuthorizationCode.class)
                .supply(field(AuthorizationCode::getId), UUID::randomUUID)
                .supply(field(AuthorizationCode::getExpiresAt), () ->
                        OffsetDateTime.ofInstant(faker.date().future(5, TimeUnit.HOURS).toInstant(), ZoneOffset.UTC))
                .supply(field(AuthorizationCode::getRedirectUri), () -> url)
                .supply(field(AuthorizationCode::getUserId), () -> faker.random().nextLong())
                .supply(field(AuthorizationCode::getClient), () -> clientFixture.createRandomOne(false))
                .toModel();
    }
}


