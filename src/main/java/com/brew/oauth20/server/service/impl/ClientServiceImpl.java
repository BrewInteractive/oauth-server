package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository _clientRepository) {
        clientRepository = _clientRepository;
    }

    @Override
    public Client getClient(UUID clientId) throws ClientNotFoundException {
        Optional<Client> client = clientRepository.findById(clientId);

        if (client == null)
            throw new ClientNotFoundException("client not found");

        return client.get();
    }
}
