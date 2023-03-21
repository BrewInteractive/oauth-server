package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ClientsGrant;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;

public class ClientsGrantFixture extends Fixture<ClientsGrant> {

    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.code, ResponseType.token};

    private final GrantFixture grantFixture;

    public ClientsGrantFixture() {
        this.grantFixture = new GrantFixture();
    }

    public ClientsGrant createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public ClientsGrant createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(clientsGrant(responseTypeOptions))
                .create();
    }

    public Set<ClientsGrant> createRandomList(Integer size) {
        return createRandomList(size, this.defaultResponseTypeOptions);
    }

    public Set<ClientsGrant> createRandomList(Integer size, ResponseType[] responseTypeOptions) {
        return Instancio.ofSet(clientsGrant(responseTypeOptions))
                .size(size)
                .create();
    }

    private Model<ClientsGrant> clientsGrant(ResponseType[] responseTypeOptions) {
        return Instancio.of(ClientsGrant.class)
                .supply(field(ClientsGrant::getGrant), () -> grantFixture.createRandomOne(responseTypeOptions))
                .toModel();
    }
}
