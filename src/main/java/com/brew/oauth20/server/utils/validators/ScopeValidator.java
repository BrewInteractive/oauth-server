package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.data.enums.Scope;
import com.brew.oauth20.server.utils.abstracts.BaseValidator;

import java.util.Arrays;
import java.util.stream.Stream;

public class ScopeValidator extends BaseValidator<String> {

    public ScopeValidator(String model) {
        super(model);
    }

    public boolean validateScope(String[] authorizedScopes) {
        return model.trim().isEmpty() ||
                model.trim().split(" ").length == 0 ||
                Stream.of(model.split(" ")).allMatch(scope -> Scope.fromValue(scope.trim()) != null && containsScope(authorizedScopes, scope.trim()));
    }

    private boolean containsScope(String[] authorizedScopes, String targetScope) {
        return Arrays.asList(authorizedScopes).contains(targetScope);
    }
}
