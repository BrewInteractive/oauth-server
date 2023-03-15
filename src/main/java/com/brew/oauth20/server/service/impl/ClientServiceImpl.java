package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Optional<Client> getClient(UUID clientId) {
        return this.clientRepository.findById(clientId);
    }
}
