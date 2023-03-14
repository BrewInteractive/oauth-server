package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.RedirectUriModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class RedirectUriModelFixture extends Fixture<RedirectUriModel> {
    public RedirectUriModel createRandomOne() {
        return Instancio.of(validModel())
                .create();
    }

    public List<RedirectUriModel> createRandomList(Integer size) {
        return Instancio.ofList(validModel())
                .size(size)
                .create();
    }

    private Model<RedirectUriModel> validModel() {
        return Instancio.of(RedirectUriModel.class)
                .supply(field(RedirectUriModel::redirectUri), () -> FakerUtils.createRandomRedirectUri(faker))
                .toModel();
    }
}