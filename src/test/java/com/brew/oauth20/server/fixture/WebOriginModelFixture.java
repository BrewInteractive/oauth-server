package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.WebOriginModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class WebOriginModelFixture extends Fixture<WebOriginModel> {


    public List<WebOriginModel> createRandomList(Integer size) {
        return Instancio.ofList(webOriginModel())
                .size(size)
                .create();
    }

    private Model<WebOriginModel> webOriginModel() {
        return Instancio.of(WebOriginModel.class)
                .supply(field(WebOriginModel::webOrigin), () -> FakerUtils.createRandomWebOrigin(faker))
                .toModel();
    }
}
