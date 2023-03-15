package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.fixture.ClientFixture;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    private ClientFixture clientFixture;

    @Test
    void shouldGetClientById() {

        clientFixture = new ClientFixture();

        var client = clientFixture.createRandomOne();

        Optional<Client> expected = Optional.of(client);

        when(clientRepository.findById(client.getId()))
                .thenReturn(expected);

        var clientService = new ClientServiceImpl(clientRepository);

        var result = clientService.getClient(client.getId());

        assertThat(expected).isEqualTo(result);
    }
}
