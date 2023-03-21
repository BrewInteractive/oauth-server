package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;

import java.util.Optional;
import java.util.UUID;

public interface ClientService {
    Optional<ClientModel> getClient(UUID clientId);
}
