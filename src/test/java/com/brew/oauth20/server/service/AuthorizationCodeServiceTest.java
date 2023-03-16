package com.brew.oauth20.server.service;

import com.brew.oauth20.server.fixture.AuthorizationCodeFixture;
import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import com.brew.oauth20.server.service.impl.AuthorizationCodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AuthorizationCodeServiceTest {
    @Mock
    private AuthorizationCodeRepository authorizationCodeRepository;

    private AuthorizationCodeFixture authorizationCodeFixture;

    @Test
    void shouldCreateAuthorizationCodeAndReturnCode() {

        authorizationCodeFixture = new AuthorizationCodeFixture();

        var authorizationCode = authorizationCodeFixture.createRandomOne();

        when(authorizationCodeRepository.findByCodeAndRedirectUri(authorizationCode.getCode(), authorizationCode.getRedirectUri()))
                .thenReturn(authorizationCode);

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository);

        var result = authorizationCodeService.createAuthorizationCode(
                authorizationCode.getUserId(),
                authorizationCode.getRedirectUri(),
                authorizationCode.getExpiresAt().toInstant().toEpochMilli(),
                authorizationCode.getClientId()
        );

        assertThat(result).isNotEmpty().isNotBlank();
        verify(authorizationCodeRepository, times(1)).save(argThat(x ->
                x.getUserId().equals(authorizationCode.getUserId())
                        && x.getRedirectUri().equals(authorizationCode.getRedirectUri())
                        && x.getClientId().equals(authorizationCode.getClientId())
                        && x.getExpiresAt().getDayOfYear() == authorizationCode.getExpiresAt().getDayOfYear()
        ));
    }

    @Test
    void shouldFindAuthorizationCodeByCodeAndRedirectUri() {

        authorizationCodeFixture = new AuthorizationCodeFixture();

        var authorizationCode = authorizationCodeFixture.createRandomOne();

        when(authorizationCodeRepository.findByCodeAndRedirectUri(authorizationCode.getCode(), authorizationCode.getRedirectUri()))
                .thenReturn(authorizationCode);

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository);

        var result = authorizationCodeService.getAuthorizationCode(authorizationCode.getCode(), authorizationCode.getRedirectUri(), false);

        assertThat(result).isEqualTo(authorizationCode.getCode());
        verify(authorizationCodeRepository, never()).save(authorizationCode);
    }

    @Test
    void shouldFindAuthorizationCodeByCodeAndRedirectUriAndSetUsedAt() {

        authorizationCodeFixture = new AuthorizationCodeFixture();

        var authorizationCode = authorizationCodeFixture.createRandomOne();

        when(authorizationCodeRepository.findByCodeAndRedirectUri(authorizationCode.getCode(), authorizationCode.getRedirectUri()))
                .thenReturn(authorizationCode);

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository);

        var result = authorizationCodeService.getAuthorizationCode(authorizationCode.getCode(), authorizationCode.getRedirectUri(), true);

        assertThat(result).isEqualTo(authorizationCode.getCode());
        verify(authorizationCodeRepository).save(authorizationCode);
    }
}
