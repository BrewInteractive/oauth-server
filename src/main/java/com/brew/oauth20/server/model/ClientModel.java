package com.brew.oauth20.server.model;

import lombok.Builder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

@Builder
public record ClientModel(
        UUID id,
        String clientId,
        String clientSecret,
        String audience,
        String issuerUri,
        Boolean issueRefreshTokens,
        int tokenExpiresInMinutes,
        int refreshTokenExpiresInDays,
        ArrayList<GrantModel> grantList,
        ArrayList<RedirectUriModel> redirectUriList,
        ArrayList<ScopeModel> scopeList
) {
    public String clientSecretDecoded() {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(clientSecret);
        return new String(decodedBytes);
    }
}
