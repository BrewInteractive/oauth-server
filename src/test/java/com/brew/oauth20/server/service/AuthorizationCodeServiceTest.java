package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.fixture.ActiveAuthorizationCodeFixture;
import com.brew.oauth20.server.fixture.AuthorizationCodeFixture;
import com.brew.oauth20.server.repository.ActiveAuthorizationCodeRepository;
import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.service.impl.AuthorizationCodeServiceImpl;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthorizationCodeServiceTest {
    @Mock
    private ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository;
    @Mock
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Mock
    private ClientRepository clientRepository;

    private AuthorizationCodeFixture authorizationCodeFixture;
    private ActiveAuthorizationCodeFixture activeAuthorizationCodeFixture;

    @Test
    void should_create_authorization_code_and_return_code() {

        activeAuthorizationCodeFixture = new ActiveAuthorizationCodeFixture();

        var activeAuthorizationCode = activeAuthorizationCodeFixture.createRandomOne();

        when(clientRepository.findByClientId(activeAuthorizationCode.getClient().getClientId()))
                .thenReturn(Optional.of(activeAuthorizationCode.getClient()));

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository, activeAuthorizationCodeRepository, clientRepository);

        var result = authorizationCodeService.createAuthorizationCode(
                activeAuthorizationCode.getUserId(),
                activeAuthorizationCode.getRedirectUri(),
                activeAuthorizationCode.getExpiresAt().toInstant().toEpochMilli(),
                activeAuthorizationCode.getClient().getClientId()
        );
        assertThat(result).isNotEmpty().isNotBlank();
        verify(authorizationCodeRepository, times(1)).save(argThat(x ->
                x.getUserId().equals(activeAuthorizationCode.getUserId())
                        && x.getRedirectUri().equals(activeAuthorizationCode.getRedirectUri())
                        && x.getClient().equals(activeAuthorizationCode.getClient())
                        && x.getExpiresAt().isAfter(OffsetDateTime.now())
        ));
    }

    @Test
    void should_find_authorization_code_by_code_and_redirect_uri() {

        activeAuthorizationCodeFixture = new ActiveAuthorizationCodeFixture();

        var activeAuthorizationCode = activeAuthorizationCodeFixture.createRandomOne();

        when(activeAuthorizationCodeRepository.findByCodeAndRedirectUri(activeAuthorizationCode.getCode(), activeAuthorizationCode.getRedirectUri()))
                .thenReturn(Optional.of(activeAuthorizationCode));

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository, activeAuthorizationCodeRepository, clientRepository);

        var result = authorizationCodeService.getAuthorizationCode(activeAuthorizationCode.getCode(), activeAuthorizationCode.getRedirectUri(), false);

        assertThat(result).isEqualTo(activeAuthorizationCode);
        verify(authorizationCodeRepository, never()).save(any());
    }

    @Test
    void should_not_find_authorization_code_by_code_and_redirect_uri() {

        authorizationCodeFixture = new AuthorizationCodeFixture();

        var authorizationCode = authorizationCodeFixture.createRandomOne();

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository, activeAuthorizationCodeRepository, clientRepository);

        var result = authorizationCodeService.getAuthorizationCode(authorizationCode.getCode(), authorizationCode.getRedirectUri(), false);

        assertThat(result).isNull();
        verify(authorizationCodeRepository, never()).save(authorizationCode);
    }

    @Test
    void should_find_authorization_code_by_code_and_redirect_uri_and_set_used_at() {

        activeAuthorizationCodeFixture = new ActiveAuthorizationCodeFixture();

        var activeAuthorizationCode = activeAuthorizationCodeFixture.createRandomOne();

        when(activeAuthorizationCodeRepository.findByCodeAndRedirectUri(activeAuthorizationCode.getCode(), activeAuthorizationCode.getRedirectUri()))
                .thenReturn(Optional.of(activeAuthorizationCode));

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository, activeAuthorizationCodeRepository, clientRepository);

        var result = authorizationCodeService.getAuthorizationCode(activeAuthorizationCode.getCode(), activeAuthorizationCode.getRedirectUri(), true);

        assertThat(result).isEqualTo(activeAuthorizationCode);
        verify(authorizationCodeRepository).save(argThat(x ->
                x.getUserId().equals(activeAuthorizationCode.getUserId())
                        && x.getRedirectUri().equals(activeAuthorizationCode.getRedirectUri())
                        && x.getClient().equals(activeAuthorizationCode.getClient())
                        && x.getExpiresAt().getDayOfYear() == activeAuthorizationCode.getExpiresAt().getDayOfYear()
        ));
    }

    @Test
    void should_create_authorization_code_throw_client_not_found_exception() {
        authorizationCodeFixture = new AuthorizationCodeFixture();

        var authorizationCode = authorizationCodeFixture.createRandomOne();

        when(clientRepository.findByClientId(authorizationCode.getClient().getClientId()))
                .thenReturn(Optional.empty());

        var authorizationCodeService = new AuthorizationCodeServiceImpl(authorizationCodeRepository, activeAuthorizationCodeRepository, clientRepository);


        Throwable thrown = catchThrowable(() -> authorizationCodeService.createAuthorizationCode(
                authorizationCode.getUserId(),
                authorizationCode.getRedirectUri(),
                authorizationCode.getExpiresAt().toInstant().toEpochMilli(),
                authorizationCode.getClient().getClientId()
        ));

        assertThat(thrown)
                .isInstanceOf(ClientNotFoundException.class);
    }
}
