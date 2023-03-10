package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.Application;
import com.brew.oauth20.server.data.Client;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
@RunWith(SpringRunner.class)
@DataJpaTest
 */
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(classes = Application.class)
public class ClientRepositoryTest {

    private static Faker faker;

    @Autowired
    ClientRepository repository;
    

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    @Test
    public void testFindById() {
        Client client = getClient();
        repository.save(client);

        Optional<Client> result = repository.findById(client.getId());
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
