package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.HookHeaderModel;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

public class HookHeaderModelFixture extends Fixture<HookHeaderModel> {
    public List<HookHeaderModel> createRandomList(Integer size) {
        return Instancio.ofList(hookHeaderModel())
                .size(size)
                .create();
    }

    private Model<HookHeaderModel> hookHeaderModel() {
        return Instancio.of(HookHeaderModel.class)
                .toModel();
    }
}
