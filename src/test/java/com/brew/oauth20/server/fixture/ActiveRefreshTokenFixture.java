package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class ActiveRefreshTokenFixture extends Fixture<ActiveRefreshToken> {

    private final ClientsUserFixture clientsUserFixture;

    public ActiveRefreshTokenFixture() {
        this.clientsUserFixture = new ClientsUserFixture();
    }

    public ActiveRefreshToken createRandomOne() {
        return Instancio.of(refreshToken())
                .create();
    }

    private Model<ActiveRefreshToken> refreshToken() {
        return Instancio.of(ActiveRefreshToken.class)
                .supply(field(ActiveRefreshToken::getClientUser), () -> clientsUserFixture.createRandomOne())
                .supply(field(ActiveRefreshToken::getToken), () -> faker.random().nextLong() + "")
                .toModel();
    }
}
