package com.brew.oauth20.server.model;

import lombok.Builder;

import java.util.UUID;


@Builder
public record WebOriginModel(UUID id, String webOrigin) {
}
