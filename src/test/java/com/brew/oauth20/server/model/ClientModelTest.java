package com.brew.oauth20.server.model;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientModelTest {
    private static Faker faker;

    @BeforeAll
    static void setUp() {
        faker = new Faker();
    }

    @Test
    void test_client_model_builder() {
        UUID id = UUID.randomUUID();
        String clientId = faker.internet().uuid();
        String decodedClientSecret = faker.lordOfTheRings().character();
        String clientSecret = Base64.getUrlEncoder().encodeToString(decodedClientSecret.getBytes(StandardCharsets.UTF_8));
        String audience = faker.internet().url();
        String issuerUri = faker.internet().url();
        int tokenExpiresInMinutes = faker.number().numberBetween(1, 60);
        int refreshTokenExpiresInDays = faker.number().numberBetween(1, 365);
        ArrayList<GrantModel> grants = new ArrayList<>();
        ArrayList<RedirectUriModel> redirectUris = new ArrayList<>();

        ClientModel client = ClientModel.builder()
                .id(id)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .audience(audience)
                .issuerUri(issuerUri)
                .issueRefreshTokens(faker.bool().bool())
                .tokenExpiresInMinutes(tokenExpiresInMinutes)
                .refreshTokenExpiresInDays(refreshTokenExpiresInDays)
                .grantList(grants)
                .redirectUriList(redirectUris)
                .build();

        assertEquals(id, client.id());
        assertEquals(clientId, client.clientId());
        assertEquals(decodedClientSecret, client.clientSecretDecoded());
        assertEquals(clientSecret, client.clientSecret());
        assertEquals(audience, client.audience());
        assertEquals(issuerUri, client.issuerUri());
        assertEquals(tokenExpiresInMinutes, client.tokenExpiresInMinutes());
        assertEquals(refreshTokenExpiresInDays, client.refreshTokenExpiresInDays());
        assertEquals(grants, client.grantList());
        assertEquals(redirectUris, client.redirectUriList());
    }

    @Test
    void test_client_secret_decoded() {
        String secret = faker.lordOfTheRings().character();
        String encodedSecret = Base64.getUrlEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));

        ClientModel client = ClientModel.builder()
                .clientSecret(encodedSecret)
                .build();

        assertEquals(secret, client.clientSecretDecoded());
    }

}