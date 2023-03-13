package com.brew.oauth20.server.model;

import lombok.Builder;


@Builder
public record GreetingModel(long id, String content) {
}
