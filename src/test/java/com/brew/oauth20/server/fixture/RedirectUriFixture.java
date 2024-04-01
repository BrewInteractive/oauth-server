package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.RedirectUri;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;

public class RedirectUriFixture extends Fixture<RedirectUri> {
    public RedirectUri createRandomOne() {
        return Instancio.of(redirectUris(null))
                .create();
    }

    public RedirectUri createRandomOne(Client client) {
        return Instancio.of(redirectUris(client))
                .create();
    }

    public Set<RedirectUri> createRandomList(Integer size) {
        return Instancio.ofSet(redirectUris(null))
                .size(size)
                .create();
    }

    private Model<RedirectUri> redirectUris(Client client) {
        return Instancio.of(RedirectUri.class)
                .supply(field(RedirectUri::getRedirectUri), () -> FakerUtils.createRandomUri(faker))
                .supply(field(RedirectUri::getClient), () -> client)
                .toModel();
    }
}
