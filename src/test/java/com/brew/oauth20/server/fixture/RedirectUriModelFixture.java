package com.brew.oauth20.server.fixture;

import br.com.six2six.fixturefactory;
import com.brew.oauth20.server.model.RedirectUriModel;

public class RedirectUriModelFixture implements com.brew.oauth20.server.fixture.Fixture<RedirectUriModel> {

    @Override
    public RedirectUriModel createRandomOne() {
        br.com.six2six.fixturefactory.Fixture.of(RedirectUriModel.class);
        return null;
    }
}
