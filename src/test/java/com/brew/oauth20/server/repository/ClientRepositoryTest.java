package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Client;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ClientRepositoryTest {

    private static Faker faker;
    @Autowired
    ClientRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    @Test
    void should_find_client_by_id() {
        // Arrange
        Client client = getClient();
        entityManager.persist(client);
        entityManager.flush();

        // Act
        Optional<Client> result = repository.findById(client.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(client.getName(), result.get().getName());
    }

    private Client getClient() {
        Client client = new Client();
        client.setName(faker.name().title());
        client.setClientId(faker.internet().password(15, 17));
        client.setClientSecret(faker.internet().password(31, 33));
        client.setCreatedAt(OffsetDateTime.now());
        client.setUpdatedAt(OffsetDateTime.now());
        client.setId(UUID.randomUUID());
        client.setRefreshTokenExpiresInDays(faker.number().numberBetween(0, 10000));
        client.setTokenExpiresInMinutes(faker.number().numberBetween(0, 10000));
        return client;
    }
}
