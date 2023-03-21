package com.brew.oauth20.server.model;

import lombok.Builder;

import java.util.ArrayList;
import java.util.UUID;


@Builder
public record ClientModel(UUID id, ArrayList<GrantModel> grantList, ArrayList<RedirectUriModel> redirectUriList) {
    public ClientModel {
        grantList = new ArrayList<>();
        redirectUriList = new ArrayList<>();
    }
}
