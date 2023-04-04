package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class ClientsUserFixture extends Fixture<ClientUser> {

    private final ClientFixture clientFixture;

    public ClientsUserFixture() {
        this.clientFixture = new ClientFixture();
    }

    public ClientUser createRandomOne() {
        return Instancio.of(clientsUser())
                .create();
    }

    private Model<ClientUser> clientsUser() {
        return Instancio.of(ClientUser.class)
                .supply(field(ClientUser::getClient), () -> clientFixture.createRandomOne(false))
                .supply(field(ClientUser::getUserId), () -> faker.random().nextLong())
                .toModel();
    }
}


