package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;

public interface ClientService {
    ClientModel getClient(String clientId);
}
