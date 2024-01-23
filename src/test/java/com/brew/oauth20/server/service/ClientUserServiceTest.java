package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.fixture.ClientsUserFixture;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.repository.ClientsUserRepository;
import com.brew.oauth20.server.service.impl.ClientUserServiceImpl;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClientUserServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientsUserRepository clientUserRepository;

    @InjectMocks
    private ClientUserServiceImpl clientUserService;

    private ClientsUserFixture clientsUserFixture;

    @Test
    void should_create_client_user() {

        clientsUserFixture = new ClientsUserFixture();

        var clientUser = clientsUserFixture.createRandomOne();

        when(clientUserRepository.findByClientIdAndUserId(clientUser.getClient().getClientId(), clientUser.getUserId()))
                .thenReturn(Optional.empty());
        when(clientRepository.findByClientId(clientUser.getClient().getClientId()))
                .thenReturn(Optional.of(clientUser.getClient()));
        when(clientUserRepository.save(argThat(x -> x.getUserId().equals(clientUser.getUserId()) && x.getClient().equals(clientUser.getClient()))))
                .thenReturn(clientUser);

        var result = clientUserService.getOrCreate(clientUser.getClient().getClientId(), clientUser.getUserId());

        assertThat(clientUser).isEqualTo(result);
    }

    @Test
    void should_create_return_existing_client_user() {

        clientsUserFixture = new ClientsUserFixture();

        var clientUser = clientsUserFixture.createRandomOne();

        var existingClientUser = clientsUserFixture.createRandomOne();

        existingClientUser.setClient(clientUser.getClient());

        existingClientUser.setUserId(clientUser.getUserId());

        when(clientUserRepository.findByClientIdAndUserId(clientUser.getClient().getClientId(), clientUser.getUserId()))
                .thenReturn(Optional.of(existingClientUser));
        when(clientRepository.findByClientId(clientUser.getClient().getClientId()))
                .thenReturn(Optional.of(existingClientUser.getClient()));
        when(clientUserRepository.save(argThat(x -> x.getUserId().equals(clientUser.getUserId()) && x.getClient().equals(clientUser.getClient()))))
                .thenReturn(existingClientUser);

        var result = clientUserService.getOrCreate(clientUser.getClient().getClientId(), clientUser.getUserId());

        assertThat(existingClientUser).isEqualTo(result);
    }

    @Test
    void should_create_throw_client_not_found() {

        clientsUserFixture = new ClientsUserFixture();

        var clientUser = clientsUserFixture.createRandomOne();

        when(clientUserRepository.findByClientIdAndUserId(clientUser.getClient().getClientId(), clientUser.getUserId()))
                .thenReturn(Optional.empty());
        when(clientRepository.findByClientId(clientUser.getClient().getClientId()))
                .thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> clientUserService.getOrCreate(clientUser.getClient().getClientId(), clientUser.getUserId()));

        assertThat(thrown).isInstanceOf(ClientNotFoundException.class);
        verify(clientRepository, never()).save(any());
    }

}