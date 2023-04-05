package com.brew.oauth20.server.model;

import lombok.Builder;

import java.util.ArrayList;
import java.util.UUID;


@Builder
public record ClientModel(
        UUID id,
        Boolean issueRefreshTokens,
        ArrayList<GrantModel> grantList,
        ArrayList<RedirectUriModel> redirectUriList
) {
}
