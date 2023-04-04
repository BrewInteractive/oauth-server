package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.exception.RefreshTokenNotFoundException;
import com.brew.oauth20.server.fixture.ClientsUserFixture;
import com.brew.oauth20.server.fixture.RefreshTokenFixture;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    private RefreshTokenFixture refreshTokenFixture;

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
        var refreshToken = service.createRefreshToken(clientUser.getClient().getClientId(), clientUser.getUserId(), token, clientUser.getClient().getRefreshTokenExpiresInDays());
        // Assert
        assertThat(refreshToken.getToken()).isEqualTo(token);
        assertThat(refreshToken.getClientUser()).isEqualTo(clientUser);
        assertThat(refreshToken.getExpiresAt().getDayOfYear()).isEqualTo(expirationDate.getDayOfYear());
        verify(refreshTokenRepository, times(1)).save(argThat(x ->
                x.getClientUser().equals(clientUser)
                        && x.getToken().equals(token)
                        && x.getExpiresAt().getDayOfYear() == expirationDate.getDayOfYear()
        ));
    }

    @Test
    void should_throws_clients_user_not_found_exception() {
        // Arrange
        Mockito.reset(clientsUserRepository);
        Mockito.reset(refreshTokenRepository);
        when(clientsUserRepository.findByClientIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());
        var service = new RefreshTokenServiceImpl(refreshTokenRepository, clientsUserRepository);
        // Act && Assert
        assertThrows(ClientsUserNotFoundException.class, () -> service.createRefreshToken("", 0L, "", 0));
    }

    @Test
    void should_revoke_refresh_token() throws RefreshTokenNotFoundException {
        // Arrange
        Mockito.reset(clientsUserRepository);
        Mockito.reset(refreshTokenRepository);
        clientsUserFixture = new ClientsUserFixture();
        refreshTokenFixture = new RefreshTokenFixture();
        var existingRefreshToken = refreshTokenFixture.createRandomOne();
        var clientUser = clientsUserFixture.createRandomOne();
        existingRefreshToken.setClientUser(clientUser);
        var newToken = faker.random().nextLong() + "";
        when(clientsUserRepository.findByClientIdAndUserId(clientUser.getClient().getClientId(), clientUser.getUserId()))
                .thenReturn(Optional.of(clientUser));
        when(refreshTokenRepository.findByToken(existingRefreshToken.getToken()))
                .thenReturn(Optional.of(existingRefreshToken));
        var service = new RefreshTokenServiceImpl(refreshTokenRepository, clientsUserRepository);
        OffsetDateTime currentDate = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expirationDate = currentDate.plusDays(clientUser.getClient().getRefreshTokenExpiresInDays());
        // Act
        var refreshToken = service.revokeRefreshToken(
                clientUser.getClient().getClientId(),
                clientUser.getUserId(),
                existingRefreshToken.getToken(),
                clientUser.getClient().getRefreshTokenExpiresInDays(),
                newToken
        );
        // Assert
        assertThat(refreshToken.getToken()).isEqualTo(newToken);
        verify(refreshTokenRepository, times(1)).save(argThat(x ->
                x.getClientUser().equals(clientUser)
                        && x.getToken().equals(newToken)
                        && x.getExpiresAt().getDayOfYear() == expirationDate.getDayOfYear()
        ));
        verify(refreshTokenRepository, times(1)).save(argThat(x ->
                x.getClientUser().equals(clientUser)
                        && x.getToken().equals(existingRefreshToken.getToken())
                        && x.getRevokedAt() != null
                        && x.getReplacedByToken().equals(refreshToken)
        ));
    }

    @Test
    void should_throws_refresh_token_not_found_exception() {
        // Arrange
        Mockito.reset(clientsUserRepository);
        Mockito.reset(refreshTokenRepository);
        when(refreshTokenRepository.findByToken(any()))
                .thenReturn(Optional.empty());
        var service = new RefreshTokenServiceImpl(refreshTokenRepository, clientsUserRepository);
        // Act && Assert
        assertThrows(RefreshTokenNotFoundException.class, () -> service.revokeRefreshToken("", 0L, "", 0, ""));
    }
}