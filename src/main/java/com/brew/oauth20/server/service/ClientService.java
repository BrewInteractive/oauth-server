package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientCredentialsModel;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.WebOriginModel;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    boolean existsByClientId(String clientId);

    ClientModel getClient(String clientId);

    ClientModel getClient(String clientId, String clientSecret);

    /**
     * @param basicAuthHeader base64 encoded string of clientId:clientSecret values
     * @return pair of first ClientId, second ClientSecret
     */
    Optional<ClientCredentialsModel> decodeClientCredentials(String basicAuthHeader);


    List<WebOriginModel> getWebOrigins(String clientId);
}
