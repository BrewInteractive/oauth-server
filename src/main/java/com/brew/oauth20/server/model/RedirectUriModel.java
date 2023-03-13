package com.brew.oauth20.server.model;

import lombok.Builder;

import java.util.UUID;


@Builder
public record RedirectUriModel(UUID id, String redirectUri) {
}
