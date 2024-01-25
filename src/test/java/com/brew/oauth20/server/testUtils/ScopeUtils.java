package com.brew.oauth20.server.testUtils;

import com.brew.oauth20.server.data.enums.Scope;

import java.util.Set;
import java.util.stream.Collectors;

public class ScopeUtils {
    public static String createScopeString(Set<Scope> scopes) {
        return scopes.stream().map(Enum::name).collect(Collectors.joining(" "));
    }
}
