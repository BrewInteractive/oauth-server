package com.brew.oauth20.server.model;

import lombok.Builder;

@Builder
public record ValidationResultModel(Boolean result, String error) {
}
