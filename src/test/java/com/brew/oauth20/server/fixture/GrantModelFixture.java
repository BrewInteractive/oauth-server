package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class GrantModelFixture extends Fixture<GrantModel> {
    @Override
    public GrantModel createRandomOne() {
        return Instancio.of(validModel())
                .create();
    }

    @Override
    public List<GrantModel> createRandomList(Integer size) {
        return Instancio.ofList(validModel())
                .size(size)
                .create();
    }

    private Model<GrantModel> validModel() {
        return Instancio.of(GrantModel.class)
                .set(field(GrantModel::responseType), FakerUtils.createRandomResponseType(faker))
                .toModel();
    }
}
