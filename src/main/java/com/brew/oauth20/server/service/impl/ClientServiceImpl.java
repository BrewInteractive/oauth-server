package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.mapper.ClientMapper;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;


    public ClientServiceImpl(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public ClientModel getClient(String clientId) {
        Optional<Client> optionalClient = clientRepository.findByClientId(clientId);
        return optionalClient.map(clientMapper::toDTO).orElse(null);
    }
}
