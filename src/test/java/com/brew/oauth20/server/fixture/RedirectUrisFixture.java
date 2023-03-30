package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.RedirectUris;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;

public class RedirectUrisFixture extends Fixture<RedirectUris> {
    public RedirectUris createRandomOne() {
        return Instancio.of(redirectUris())
                .create();
    }

    public Set<RedirectUris> createRandomList(Integer size) {
        return Instancio.ofSet(redirectUris())
                .size(size)
                .create();
    }

    private Model<RedirectUris> redirectUris() {
        return Instancio.of(RedirectUris.class)
                .supply(field(RedirectUris::getRedirectUri), () -> FakerUtils.createRandomRedirectUri(faker))
                .toModel();
    }
}
