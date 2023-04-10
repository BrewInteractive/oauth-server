package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class ActiveRefreshTokenFixture extends Fixture<ActiveRefreshToken> {

    private final ClientsUserFixture clientsUserFixture;

    public ActiveRefreshTokenFixture() {
        this.clientsUserFixture = new ClientsUserFixture();
    }

    public ActiveRefreshToken createRandomOne() {
        return Instancio.of(refreshToken(null))
                .create();
    }
    public ActiveRefreshToken createRandomOne(ClientUser clientUser) {
        return Instancio.of(refreshToken(clientUser))
                .create();
    }

    private Model<ActiveRefreshToken> refreshToken(ClientUser clientUser) {
        return Instancio.of(ActiveRefreshToken.class)
                .supply(field(ActiveRefreshToken::getClientUser), () -> clientUser != null ? clientUser : clientsUserFixture.createRandomOne())
                .supply(field(ActiveRefreshToken::getCreatedAt), () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(ActiveRefreshToken::getUpdatedAt), () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(ActiveRefreshToken::getExpiresAt), () -> faker.date().future(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(ActiveRefreshToken::getToken), () -> faker.random().nextLong() + "")
                .toModel();
    }
}
