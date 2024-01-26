package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class RefreshTokenFixture extends Fixture<RefreshToken> {

    private final ClientUserFixture clientUserFixture;

    public RefreshTokenFixture() {
        this.clientUserFixture = new ClientUserFixture();
    }

    public RefreshToken createRandomOne() {
        return Instancio.of(refreshToken())
                .create();
    }

    private Model<RefreshToken> refreshToken() {
        return Instancio.of(RefreshToken.class)
                .supply(field(RefreshToken::getClientUser), () -> clientUserFixture.createRandomOne())
                .supply(field(RefreshToken::getToken), () -> faker.lordOfTheRings().character())
                .toModel();
    }
}
