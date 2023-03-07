package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Client;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void testFindById() {
        Client entity = new Client();
        entity.setName(faker.name().title());
        entity.setClientId(faker.internet().password(15, 17));
        entity.setClientSecret(faker.internet().password(31, 33));
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        entity.setId(UUID.randomUUID());
        entity.setRefreshTokenExpiresInDays(faker.number().numberBetween(0, 10000));
        entity.setTokenExpiresInMinutes(faker.number().numberBetween(0, 10000));
        entityManager.persist(entity);
        Optional<Client> result = repository.findById(entity.getId());
        assertTrue(result.isPresent());
        assertEquals(entity.getName(), result.get().getName());
    }
}
