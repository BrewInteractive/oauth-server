package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.repository.ClientsUserRepository;
import com.brew.oauth20.server.service.ClientUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientUserServiceImpl implements ClientUserService {

    private final ClientsUserRepository clientsUserRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientUserServiceImpl(ClientsUserRepository clientsUserRepository,
                                 ClientRepository clientRepository) {
        this.clientsUserRepository = clientsUserRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientUser create(String clientId, String userId) {
        var existingClientUser = clientsUserRepository.findByClientIdAndUserId(clientId, userId);

        if (existingClientUser.isPresent())
            return existingClientUser.get();

        var client = clientRepository.findByClientId(clientId);

        if (client.isEmpty())
            throw new ClientNotFoundException(clientId);

        return clientsUserRepository.save(ClientUser.builder()
                .client(client.get())
                .userId(userId)
                .build());
    }
}
