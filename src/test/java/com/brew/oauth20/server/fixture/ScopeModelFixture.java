package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.enums.Scope;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.ScopeModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class ScopeModelFixture extends Fixture<GrantModel> {
    public List<ScopeModel> createRandomList(Integer size, Scope[] scopeOptions) {
        return Instancio.ofList(scopeModel(scopeOptions))
                .size(size)
                .create();
    }

    private Model<ScopeModel> scopeModel(Scope[] scopeOptions) {
        return Instancio.of(ScopeModel.class)
                .supply(field(ScopeModel::scope), () -> FakerUtils.createRandomEnum(faker, scopeOptions))
                .toModel();
    }
}
