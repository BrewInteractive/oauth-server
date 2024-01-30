package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.ClientGrant;
import com.brew.oauth20.server.data.Grant;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;

public class ClientGrantFixture extends Fixture<ClientGrant> {

    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.code, ResponseType.token};

    private final GrantType[] defaultGrantTypeOptions = new GrantType[]{GrantType.authorization_code,
            GrantType.client_credentials, GrantType.refresh_token};
    private final GrantFixture grantFixture;

    public ClientGrantFixture() {
        this.grantFixture = new GrantFixture();
    }

    public ClientGrant createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public ClientGrant createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(clientGrant(responseTypeOptions, defaultGrantTypeOptions))
                .create();
    }

    public ClientGrant createRandomOne(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions) {
        return Instancio.of(clientGrant(responseTypeOptions, grantTypeOptions))
                .create();
    }


    public Set<ClientGrant> createRandomList(Integer size, ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions) {
        return Instancio.ofSet(clientGrant(responseTypeOptions, grantTypeOptions))
                .size(size)
                .create();
    }

    public ClientGrant createRandomOne(Client client, Grant grant) {
        return Instancio.of(clientGrant(client, grant))
                .create();
    }

    private Model<ClientGrant> clientGrant(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions) {
        return Instancio.of(ClientGrant.class)
                .supply(field(ClientGrant::getGrant), () -> grantFixture.createRandomOne(responseTypeOptions, grantTypeOptions))
                .supply(field(ClientGrant::getClient), () -> null)
                .toModel();
    }

    private Model<ClientGrant> clientGrant(Client client, Grant grant) {
        return Instancio.of(ClientGrant.class)
                .supply(field(ClientGrant::getGrant), () -> grant)
                .supply(field(ClientGrant::getClient), () -> client)
                .toModel();
    }
}
