package com.brew.oauth20.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ClientCredentialsModel {
    private String clientId;
    private String clientSecret;
}
