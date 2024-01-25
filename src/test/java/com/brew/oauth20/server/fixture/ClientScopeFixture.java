package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.ClientGrant;
import com.brew.oauth20.server.data.ClientScope;
import com.brew.oauth20.server.data.enums.Scope;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.HashSet;
import java.util.Set;

import static org.instancio.Select.field;

public class ClientScopeFixture extends Fixture<ClientScope> {
    public Set<ClientScope> createRandomList(Integer size, Scope[] scopeOptions) {
        return Instancio.ofSet(clientsScope(scopeOptions))
                .size(size)
                .create();
    }

    public Set<ClientScope> createRandomUniqueList(Scope[] scopeOptions) {
        var uniqueScopes = FakerUtils.createRandomScopeList(faker, scopeOptions);
        var clientScopes = new HashSet<ClientScope>();
        for (var scope : uniqueScopes) {
            var clientScope = Instancio.of(clientsScope(scope))
                    .create();
            clientScopes.add(clientScope);
        }
        return clientScopes;
    }

    private Model<ClientScope> clientsScope(Scope[] scopeOptions) {
        return Instancio.of(ClientScope.class)
                .supply(field(ClientScope::getScope), () -> FakerUtils.createRandomScope(faker, scopeOptions))
                .supply(field(ClientGrant::getClient), () -> null)
                .toModel();
    }

    private Model<ClientScope> clientsScope(Scope scope) {
        return Instancio.of(ClientScope.class)
                .supply(field(ClientScope::getScope), () -> scope)
                .supply(field(ClientGrant::getClient), () -> null)
                .toModel();
    }
}
