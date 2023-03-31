package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class GrantModelFixture extends Fixture<GrantModel> {
    private final ResponseType[] defaultResponseTypeOptions = ResponseType.values();
    private final GrantType[] defaultGrantTypeOptions = GrantType.values();

    public GrantModel createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public GrantModel createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(grantModel(responseTypeOptions, this.defaultGrantTypeOptions))
                .create();
    }

    public GrantModel createRandomOne(GrantType[] grantTypeOptions) {
        return Instancio.of(grantModel(this.defaultResponseTypeOptions, grantTypeOptions))
                .create();
    }

    public List<GrantModel> createRandomList(Integer size) {
        return createRandomList(size, this.defaultResponseTypeOptions, this.defaultGrantTypeOptions);
    }

    public List<GrantModel> createRandomList(Integer size, ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions) {
        return Instancio.ofList(grantModel(responseTypeOptions, grantTypeOptions))
                .size(size)
                .create();
    }

    private Model<GrantModel> grantModel(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions) {
        return Instancio.of(GrantModel.class)
                .supply(field(GrantModel::responseType), () -> FakerUtils.createRandomResponseType(faker, responseTypeOptions))
                .supply(field(GrantModel::grantType), () -> FakerUtils.createRandomGrantType(faker, grantTypeOptions))
                .toModel();
    }
}
