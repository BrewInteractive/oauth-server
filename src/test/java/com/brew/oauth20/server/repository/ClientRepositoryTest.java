package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.fixture.ClientFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    ClientRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    private ClientFixture clientFixture;

    @Test
    void should_find_client_by_id() {
        // Arrange
        clientFixture = new ClientFixture();
        var client = clientFixture.createRandomOne();
        entityManager.persist(client);
        entityManager.flush();

        // Act
        Optional<Client> result = repository.findById(client.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(client.getName(), result.get().getName());
    }
}
