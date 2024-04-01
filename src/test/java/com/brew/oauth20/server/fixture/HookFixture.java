package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.Hook;
import com.brew.oauth20.server.data.enums.HookType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.instancio.Select.field;

public class HookFixture extends Fixture<Hook> {
    public Hook createRandomOne(HookType[] hookTypeOptions) {
        return Instancio.of(hook(hookTypeOptions))
                .create();
    }

    public Set<Hook> createRandomList(Integer size, HookType[] hookTypeOptions) {
        return Instancio.ofSet(hook(hookTypeOptions))
                .size(size)
                .create();
    }

    public Set<Hook> createRandomUniqueList(Client client, HookType[] hookTypeOptions) {
        var uniqueHookTypes = FakerUtils.createRandomEnumList(faker, hookTypeOptions);
        var hooks = new HashSet<Hook>();
        for (var hookType : uniqueHookTypes) {
            var hook = Instancio.of(hook(client, hookType))
                    .create();
            hooks.add(hook);
        }
        return hooks;
    }

    private Model<Hook> hook(HookType[] hookTypeOptions) {
        return Instancio.of(Hook.class)
                .supply(field(Hook::getEndpoint), () -> FakerUtils.createRandomUri(faker))
                .supply(field(Hook::getHookType), () -> FakerUtils.createRandomEnum(faker, hookTypeOptions))
                .supply(field(Hook::getHookHeaders), () -> new LinkedHashSet<>())
                .supply(field(Hook::getClient), () -> null)
                .toModel();
    }

    private Model<Hook> hook(Client client, HookType hookType) {
        return Instancio.of(Hook.class)
                .supply(field(Hook::getEndpoint), () -> FakerUtils.createRandomUri(faker))
                .supply(field(Hook::getHookType), () -> hookType)
                .supply(field(Hook::getHookHeaders), () -> new LinkedHashSet<>())
                .supply(field(Hook::getClient), () -> client)
                .toModel();
    }
}
