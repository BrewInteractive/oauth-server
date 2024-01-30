package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.ClientUserScope;
import com.brew.oauth20.server.data.enums.Scope;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class ClientUserScopeFixture extends Fixture<ClientUserScope> {
    public ClientUserScope createRandomOne(ClientUser clientUser, Scope scope) {
        return Instancio.of(clientUserScope(clientUser, scope))
                .create();
    }

    private Model<ClientUserScope> clientUserScope(ClientUser clientUser, Scope scope) {
        return Instancio.of(ClientUserScope.class)
                .supply(field(ClientUserScope::getClientUser), () -> clientUser)
                .supply(field(ClientUserScope::getScope), () -> scope)
                .toModel();
    }
}
