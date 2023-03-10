package com.brew.oauth20.server.model;

import java.util.ArrayList;
import java.util.UUID;

public record ClientModel(UUID id, ArrayList<GrantModel> grantList, ArrayList<RedirectUriModel> redirectUriList) {
}
