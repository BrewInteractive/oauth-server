package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ClientsUser;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class ClientsUserFixture extends Fixture<ClientsUser> {

    private final ClientFixture clientFixture;

    public ClientsUserFixture() {
        this.clientFixture = new ClientFixture();
    }

    public ClientsUser createRandomOne() {
        return Instancio.of(clientsUser())
                .create();
    }

    private Model<ClientsUser> clientsUser() {
        return Instancio.of(ClientsUser.class)
                .supply(field(ClientsUser::getClient), () -> clientFixture.createRandomOne(false))
                .supply(field(ClientsUser::getUserId), () -> faker.random().nextLong())
                .toModel();
    }
}


