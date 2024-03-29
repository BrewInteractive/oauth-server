package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.enums.HookType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.HookModel;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class HookModelFixture extends Fixture<HookModel> {
    private final HookHeaderModelFixture hookHeaderModelFixture;

    public HookModelFixture() {
        this.hookHeaderModelFixture = new HookHeaderModelFixture();
    }

    public HookModel createRandomOne(HookType hookType, int hookHeaderSize) {
        return Instancio.of(hookModel(hookType, hookHeaderSize))
                .create();
    }

    private Model<HookModel> hookModel(HookType hookType, int hookHeaderSize) {
        return Instancio.of(HookModel.class)
                .supply(field(HookModel::endpoint), () -> faker.internet().url())
                .supply(field(HookModel::hookType), () -> hookType)
                .supply(field(HookModel::hookHeaderList), () -> hookHeaderModelFixture.createRandomList(hookHeaderSize))
                .toModel();
    }
}
