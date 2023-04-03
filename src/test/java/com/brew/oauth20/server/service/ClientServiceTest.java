package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.fixture.ClientFixture;
import com.brew.oauth20.server.mapper.ClientMapper;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @MockBean
    private ClientMapper clientMapper;

    private ClientFixture clientFixture;

    @Test
    void should_get_client_by_client_id() {

        clientFixture = new ClientFixture();

        var client = clientFixture.createRandomOne(true);

        Optional<Client> optionalClient = Optional.of(client);

        when(clientRepository.findByClientId(client.getClientId()))
                .thenReturn(optionalClient);

        var expected = optionalClient.map(clientMapper::toDTO).orElse(null);

        var clientService = new ClientServiceImpl(clientRepository, clientMapper);

        var result = clientService.getClient(client.getClientId());

        assertThat(expected).isEqualTo(result);
    }

    @Test
    void should_decode_client_credentials(){
        clientFixture = new ClientFixture();

        var client = clientFixture.createRandomOne(false);

        var decodeHeader =client.getClientId()+":"+client.getClientSecret();
        String encodedHeader =Base64.getEncoder().withoutPadding().encodeToString(decodeHeader.getBytes());

        var clientService = new ClientServiceImpl(clientRepository, clientMapper);

        var pairResult = clientService.decodeClientCredentials(encodedHeader);

        assertThat(pairResult).isNotEmpty();
        assertThat(pairResult.get().getFirst()).isEqualTo(client.getClientId());
        assertThat(pairResult.get().getSecond()).isEqualTo(client.getClientSecret());
    }

    @Test
    void should_throw_exception_on_decode_client_credentials(){
        String encodedHeader =Base64.getEncoder().withoutPadding().encodeToString("malformed-header".getBytes());

        var clientService = new ClientServiceImpl(clientRepository, clientMapper);

        var pairResult = clientService.decodeClientCredentials(encodedHeader);

        assertThat(pairResult).isEmpty();
    }
}
