package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;
import org.springframework.data.util.Pair;

import java.util.Optional;

public interface ClientService {
    ClientModel getClient(String clientId);

    /**
     * @param basicAuthHeader base64 encoded string of clientId:clientSecret values
     * @return pair of first ClientId, second ClientSecret
     */
    Optional<Pair<String, String>> decodeClientCredentials(String basicAuthHeader);
}
