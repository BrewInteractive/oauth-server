package com.brew.oauth20.server.model;

import lombok.Builder;

@Builder
public record UpdateClientLogoResponseModel(String client_id, String client_logo_url) {
}

