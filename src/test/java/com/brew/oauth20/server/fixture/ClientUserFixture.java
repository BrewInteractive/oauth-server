package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.instancio.Select.field;

public class ClientUserFixture extends Fixture<ClientUser> {

    private final String userIdPrefix = "did:tmrwid:";
    private final ClientFixture clientFixture;

    public ClientUserFixture() {
        this.clientFixture = new ClientFixture();
    }

    public ClientUser createRandomOne() {
        return Instancio.of(clientsUser(null))
                .create();
    }

    public ClientUser createRandomOne(Client client) {
        return Instancio.of(clientsUser(client))
                .create();
    }

    private Model<ClientUser> clientsUser(Client client) {
        return Instancio.of(ClientUser.class)
                .supply(field(ClientUser::getId), UUID::randomUUID)
                .supply(field(ClientUser::getClient), () -> client != null ? client : clientFixture.createRandomOne(false))
                .supply(field(ClientUser::getClientUserScopes), () -> new LinkedHashSet<>())
                .supply(field(ClientUser::getUserId), () -> userIdPrefix + faker.random().nextLong(10))
                .supply(field(ClientUser::getRefreshTokens), () -> new LinkedHashSet<>())
                .supply(field(ClientUser::getCreatedAt), () -> OffsetDateTime.now())
                .supply(field(ClientUser::getUpdatedAt), () -> OffsetDateTime.now())
                .toModel();
    }
}


