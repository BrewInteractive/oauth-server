package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;
import java.util.UUID;

import static org.instancio.Select.field;

public class ClientFixture extends Fixture<Client> {

    private final Integer defaultClientsGrantSize = 1;
    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.CODE, ResponseType.TOKEN};
    private final Integer defaultRedirectUriSize = 1;

    private final ClientsGrantFixture clientsGrantFixture;
    private final RedirectUrisFixture redirectUriModelFixture;

    public ClientFixture() {
        super();
        this.clientsGrantFixture = new ClientsGrantFixture();
        this.redirectUriModelFixture = new RedirectUrisFixture();
    }

    public Client createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public Client createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(client(responseTypeOptions))
                .create();
    }

    public Set<Client> createRandomList(Integer size) {
        return createRandomList(size, this.defaultResponseTypeOptions);
    }

    public Set<Client> createRandomList(Integer size, ResponseType[] responseTypeOptions) {
        return Instancio.ofSet(client(responseTypeOptions))
                .size(size)
                .create();
    }

    private Model<Client> client(ResponseType[] responseTypeOptions) {
        return Instancio.of(Client.class)
                .supply(field(Client::getName), () -> faker.name().title())
                .supply(field(Client::getId), () -> UUID.randomUUID())
                .supply(field(Client::getClientsGrants), () -> clientsGrantFixture.createRandomList(this.defaultClientsGrantSize, responseTypeOptions))
                .supply(field(Client::getRedirectUris), () -> redirectUriModelFixture.createRandomList(this.defaultRedirectUriSize))
                .toModel();
    }
}