package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;

import java.util.UUID;

public interface ClientService {
    ClientModel getClient(UUID clientId);
}
