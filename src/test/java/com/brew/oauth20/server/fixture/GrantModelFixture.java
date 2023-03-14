package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class GrantModelFixture extends Fixture<GrantModel> {
    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.code, ResponseType.token};

    public GrantModel createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public GrantModel createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(grantModel(responseTypeOptions))
                .create();
    }

    public List<GrantModel> createRandomList(Integer size) {
        return createRandomList(size, this.defaultResponseTypeOptions);
    }

    public List<GrantModel> createRandomList(Integer size, ResponseType[] responseTypeOptions) {
        return Instancio.ofList(grantModel(responseTypeOptions))
                .size(size)
                .create();
    }

    private Model<GrantModel> grantModel(ResponseType[] responseTypeOptions) {
        return Instancio.of(GrantModel.class)
                .set(field(GrantModel::responseType), FakerUtils.createRandomResponseType(faker, responseTypeOptions))
                .toModel();
    }
}
