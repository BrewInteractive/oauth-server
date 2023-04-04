package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ClientGrant;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;

public class ClientGrantFixture extends Fixture<ClientGrant> {

    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.code, ResponseType.token};

    private final GrantFixture grantFixture;

    public ClientGrantFixture() {
        this.grantFixture = new GrantFixture();
    }

    public ClientGrant createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public ClientGrant createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(clientsGrant(responseTypeOptions))
                .create();
    }

    public Set<ClientGrant> createRandomList(Integer size) {
        return createRandomList(size, this.defaultResponseTypeOptions);
    }

    public Set<ClientGrant> createRandomList(Integer size, ResponseType[] responseTypeOptions) {
        return Instancio.ofSet(clientsGrant(responseTypeOptions))
                .size(size)
                .create();
    }

    private Model<ClientGrant> clientsGrant(ResponseType[] responseTypeOptions) {
        return Instancio.of(ClientGrant.class)
                .supply(field(ClientGrant::getGrant), () -> grantFixture.createRandomOne(responseTypeOptions))
                .toModel();
    }
}
