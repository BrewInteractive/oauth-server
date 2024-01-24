package com.brew.oauth20.server.model;

import com.brew.oauth20.server.data.enums.Scope;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ScopeModel(UUID id, Scope scope) {
}
