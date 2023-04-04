package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.fixture.ClientsUserFixture;
import com.brew.oauth20.server.repository.ClientsUserRepository;
import com.brew.oauth20.server.repository.RefreshTokenRepository;
import com.brew.oauth20.server.service.impl.RefreshTokenServiceImpl;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RefreshTokenServiceTest {
    private static Faker faker;
    @Mock
    private ClientsUserRepository clientsUserRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    private ClientsUserFixture clientsUserFixture;

    @BeforeAll
    public static void init() {
        faker = new Faker();
    }

    @Test
    void should_create_and_return_refresh_token() throws ClientsUserNotFoundException {
        // Arrange
        Mockito.reset(clientsUserRepository);
        clientsUserFixture = new ClientsUserFixture();
        var clientUser = clientsUserFixture.createRandomOne();
        when(clientsUserRepository.findByClientIdAndUserId(clientUser.getClient().getClientId(), clientUser.getUserId()))
                .thenReturn(Optional.of(clientUser));
        var service = new RefreshTokenServiceImpl(refreshTokenRepository, clientsUserRepository);
        OffsetDateTime currentDate = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expirationDate = currentDate.plusDays(clientUser.getClient().getRefreshTokenExpiresInDays());
        var token = faker.random().nextLong() + "";
        // Act
        service.createRefreshToken(clientUser.getClient().getClientId(), clientUser.getUserId(), token, clientUser.getClient().getRefreshTokenExpiresInDays());
        // Assert
        verify(refreshTokenRepository, times(1)).save(argThat(x ->
                x.getClientUser().equals(clientUser)
                        && x.getToken().equals(token)
                        && x.getExpiresAt().getDayOfYear() == expirationDate.getDayOfYear()
        ));
    }

    @Test
    void should_throws_clients_user_not_found_exception() {
        // Arrange
        var service = new RefreshTokenServiceImpl(refreshTokenRepository, clientsUserRepository);
        // Act && Assert
        assertThrows(ClientsUserNotFoundException.class, () -> service.createRefreshToken("", 0L, "", 0));
    }
}
