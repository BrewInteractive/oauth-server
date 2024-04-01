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
    private final Integer defaultHookHeaderSize = 1;

    public Set<Hook> createRandomList(Integer size, HookType[] hookTypeOptions) {
        return createRandomList(size, hookTypeOptions, defaultHookHeaderSize);
    }

    public Set<Hook> createRandomList(Integer size, HookType[] hookTypeOptions, Integer hookHeaderSize) {
        return Instancio.ofSet(hook(hookTypeOptions, hookHeaderSize))
                .size(size)
                .create();
    }

    public Set<Hook> createRandomUniqueList(Client client, HookType[] hookTypeOptions) {
        return createRandomUniqueList(client, hookTypeOptions, defaultHookHeaderSize);
    }

    public Set<Hook> createRandomUniqueList(Client client, HookType[] hookTypeOptions, Integer hookHeaderSize) {
        var uniqueHookTypes = FakerUtils.createRandomEnumList(faker, hookTypeOptions);
        var hooks = new HashSet<Hook>();
        for (var hookType : uniqueHookTypes) {
            var hook = Instancio.of(hook(client, hookType, hookHeaderSize))
                    .create();
            hooks.add(hook);
        }
        return hooks;
    }

    private Model<Hook> hook(HookType[] hookTypeOptions, Integer hookHeaderSize) {
        return Instancio.of(Hook.class)
                .supply(field(Hook::getEndpoint), () -> FakerUtils.createRandomUri(faker))
                .supply(field(Hook::getHookType), () -> FakerUtils.createRandomEnum(faker, hookTypeOptions))
                .supply(field(Hook::getHookHeaders), () -> new LinkedHashSet<>())
                .supply(field(Hook::getClient), () -> null)
                .toModel();
    }

    private Model<Hook> hook(Client client, HookType hookType, Integer hookHeaderSize) {
        return Instancio.of(Hook.class)
                .supply(field(Hook::getEndpoint), () -> FakerUtils.createRandomUri(faker))
                .supply(field(Hook::getHookType), () -> hookType)
                .supply(field(Hook::getHookHeaders), () -> new LinkedHashSet<>())
                .supply(field(Hook::getClient), () -> client)
                .toModel();
    }
}
