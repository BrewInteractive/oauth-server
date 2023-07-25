package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.WebOrigin;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class WebOriginFixture extends Fixture<WebOrigin> {
    public List<WebOrigin> createRandomList(Integer size) {
        return Instancio.ofList(webOriginModel())
                .size(size)
                .create();
    }

    private Model<WebOrigin> webOriginModel() {
        return Instancio.of(WebOrigin.class)
                .supply(field(WebOrigin::getWebOrigin), () -> FakerUtils.createRandomWebOrigin(faker))
                .toModel();
    }
}
