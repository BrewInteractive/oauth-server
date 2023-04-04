package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.RedirectUri;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;

public class RedirectUriFixture extends Fixture<RedirectUri> {
    public RedirectUri createRandomOne() {
        return Instancio.of(redirectUris())
                .create();
    }

    public Set<RedirectUri> createRandomList(Integer size) {
        return Instancio.ofSet(redirectUris())
                .size(size)
                .create();
    }

    private Model<RedirectUri> redirectUris() {
        return Instancio.of(RedirectUri.class)
                .supply(field(RedirectUri::getRedirectUri), () -> FakerUtils.createRandomRedirectUri(faker))
                .toModel();
    }
}
