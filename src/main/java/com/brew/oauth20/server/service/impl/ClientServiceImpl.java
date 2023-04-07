package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.mapper.ClientMapper;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private  ClientRepository clientRepository;
    @Autowired
    private  ClientMapper clientMapper;
    Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);



    @Override
    public ClientModel getClient(String clientId) {
        Optional<Client> optionalClient = clientRepository.findByClientId(clientId);
        return optionalClient.map(clientMapper::toDTO).orElse(null);
    }

    @Override
    public ClientModel getClient(String clientId, String clientSecret) {
        Optional<Client> optionalClient = clientRepository.findByClientIdAndClientSecret(clientId, clientSecret);
        return optionalClient.map(clientMapper::toDTO).orElse(null);
    }

    @Override
    public Optional<Pair<String, String>> decodeClientCredentials(String basicAuthHeader) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(basicAuthHeader);
            String decodedAuthHeaderValue = new String(decodedBytes);
            String[] values = decodedAuthHeaderValue.split(":");
            String firstValue = Arrays.stream(values).findFirst().orElseThrow(() -> new NoSuchElementException("Auth header is malformed"));
            String secondValue = Arrays.stream(values).skip(1).findFirst().orElseThrow(() -> new NoSuchElementException("Auth header is malformed"));
            return Optional.of(Pair.of(firstValue, secondValue));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
