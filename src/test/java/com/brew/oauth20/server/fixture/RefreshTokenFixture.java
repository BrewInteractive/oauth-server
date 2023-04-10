package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class RefreshTokenFixture extends Fixture<RefreshToken> {

    private final ClientsUserFixture clientsUserFixture;

    public RefreshTokenFixture() {
        this.clientsUserFixture = new ClientsUserFixture();
    }

    public RefreshToken createRandomOne() {
        return Instancio.of(refreshToken())
                .create();
    }

    public RefreshToken createRandomOne(ClientUser clientUser) {
        return Instancio.of(refreshToken(clientUser))
                .create();
    }

    private Model<RefreshToken> refreshToken() {
        return Instancio.of(RefreshToken.class)
                .supply(field(RefreshToken::getClientUser), () -> clientsUserFixture.createRandomOne())
                .supply(field(RefreshToken::getToken), () -> faker.lordOfTheRings().character())
                .toModel();
    }

    private Model<RefreshToken> refreshToken(ClientUser clientUser) {
        return Instancio.of(RefreshToken.class)
                .supply(field(RefreshToken::getClientUser), () -> clientUser)
                .supply(field(RefreshToken::getToken), () -> faker.lordOfTheRings().character())
                .toModel();
    }
}
