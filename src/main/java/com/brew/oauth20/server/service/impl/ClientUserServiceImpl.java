package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.repository.ClientUserRepository;
import com.brew.oauth20.server.service.ClientUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientUserServiceImpl implements ClientUserService {

    private final ClientUserRepository clientUserRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientUserServiceImpl(ClientUserRepository clientUserRepository,
                                 ClientRepository clientRepository) {
        this.clientUserRepository = clientUserRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientUser getOrCreate(String clientId, String userId) {
        var existingClientUser = clientUserRepository.findByClientIdAndUserId(clientId, userId);

        if (existingClientUser.isPresent())
            return existingClientUser.get();

        var client = clientRepository.findByClientId(clientId);

        if (client.isEmpty())
            throw new ClientNotFoundException(clientId);

        var clientUser = ClientUser.builder()
                .client(client.get())
                .userId(userId)
                .build();

        return clientUserRepository.save(clientUser);
    }
}
