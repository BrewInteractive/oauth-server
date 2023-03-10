package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.exception.ClientNotFoundException;

import java.util.UUID;

public interface ClientService {
    Client getClient(UUID clientId) throws ClientNotFoundException;
}
