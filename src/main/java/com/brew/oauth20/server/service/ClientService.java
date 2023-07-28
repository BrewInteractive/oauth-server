package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.WebOriginModel;
import org.springframework.data.util.Pair;

import java.io.IOException;
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
    Optional<Pair<String, String>> decodeClientCredentials(String basicAuthHeader);

    String setClientLogo(String clientId, String logoFile) throws IOException;

    List<WebOriginModel> getWebOrigins(String clientId);
}
