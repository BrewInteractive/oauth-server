package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.repository.ClientMapper;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;


    public ClientServiceImpl(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public ClientModel getClient(UUID clientId) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        return optionalClient.map(clientMapper::toDTO).orElse(null);
    }
}
