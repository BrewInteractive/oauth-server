package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientService {
    Optional<Client> getClient(UUID clientId);
}
