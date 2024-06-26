package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.RefreshTokenNotFoundException;
import com.brew.oauth20.server.fixture.ActiveRefreshTokenFixture;
import com.brew.oauth20.server.fixture.ClientUserFixture;
import com.brew.oauth20.server.repository.ActiveRefreshTokenRepository;
import com.brew.oauth20.server.repository.RefreshTokenRepository;
import com.brew.oauth20.server.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
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
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RefreshTokenServiceTest {
    private ClientUserFixture clientUserFixture;
    private ActiveRefreshTokenFixture activeRefreshTokenFixture;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private ActiveRefreshTokenRepository activeRefreshTokenRepository;

    @BeforeEach
    public void init() {
        clientUserFixture = new ClientUserFixture();
        activeRefreshTokenFixture = new ActiveRefreshTokenFixture();
        Mockito.reset(refreshTokenRepository);
        Mockito.reset(activeRefreshTokenRepository);
    }

    @Test
    void should_create_and_return_refresh_token() {
        // Arrange
        var clientUser = clientUserFixture.createRandomOne();
        var service = new RefreshTokenServiceImpl(refreshTokenRepository, activeRefreshTokenRepository);
        OffsetDateTime currentDate = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expirationDate = currentDate.plusDays(clientUser.getClient().getRefreshTokenExpiresInDays());

        // Act
        var refreshToken = service.createRefreshToken(clientUser, clientUser.getClient().getRefreshTokenExpiresInDays());

        // Assert
        assertThat(refreshToken.getToken()).isNotBlank();
        assertThat(refreshToken.getClientUser()).isEqualTo(clientUser);
        assertThat(refreshToken.getExpiresAt().getDayOfYear()).isEqualTo(expirationDate.getDayOfYear());
        verify(refreshTokenRepository, times(1)).save(argThat(x ->
                x.getClientUser().equals(clientUser)
                        && !x.getToken().isBlank()
                        && x.getExpiresAt().getDayOfYear() == expirationDate.getDayOfYear()
        ));
    }

    @Test
    void should_revoke_refresh_token() throws RefreshTokenNotFoundException {
        // Arrange
        var activeRefreshToken = activeRefreshTokenFixture.createRandomOne();
        var clientUser = clientUserFixture.createRandomOne();
        activeRefreshToken.setClientUser(clientUser);

        when(activeRefreshTokenRepository.findByToken(activeRefreshToken.getToken()))
                .thenReturn(Optional.of(activeRefreshToken));
        OffsetDateTime currentDate = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expirationDate = currentDate.plusDays(clientUser.getClient().getRefreshTokenExpiresInDays());

        // Act
        var refreshToken = new RefreshTokenServiceImpl(refreshTokenRepository,
                activeRefreshTokenRepository
        ).revokeRefreshToken(
                clientUser.getClient().getClientId(),
                activeRefreshToken.getToken(),
                clientUser.getClient().getRefreshTokenExpiresInDays()
        );

        // Assert
        assertThat(refreshToken.getToken()).isNotBlank();
        verify(refreshTokenRepository, times(1)).save(argThat(x ->
                x.getClientUser().equals(clientUser)
                        && !x.getToken().isBlank()
                        && x.getExpiresAt().getDayOfYear() == expirationDate.getDayOfYear()
        ));
        verify(refreshTokenRepository, times(1)).save(argThat(x ->
                x.getClientUser().equals(clientUser)
                        && x.getToken().equals(activeRefreshToken.getToken())
                        && x.getRevokedAt() != null
                        && x.getReplacedByToken().equals(refreshToken)
        ));
    }

    @Test
    void should_throws_refresh_token_not_found_exception() {
        // Arrange
        when(activeRefreshTokenRepository.findByToken(any()))
                .thenReturn(Optional.empty());
        var service = new RefreshTokenServiceImpl(refreshTokenRepository, activeRefreshTokenRepository);

        // Act && Assert
        assertThrows(RefreshTokenNotFoundException.class, () -> service.revokeRefreshToken("", "", 0));
    }
}
