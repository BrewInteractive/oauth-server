package com.brew.oauth20.server.service;

import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.service.impl.TokenServiceImpl;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TokenServiceTest {

    private static final String userIdPrefix = "did:tmrwid:";
    private static ClientModelFixture clientModelFixture;
    private static Faker faker;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private TokenServiceImpl tokenService;

    @BeforeAll
    public static void init() {
        faker = new Faker();
        clientModelFixture = new ClientModelFixture();
    }

    private static Stream<Arguments> should_generate_token_without_user_id() {

        return Stream.of(
                Arguments.of(
                        clientModelFixture.createRandomOne(false),
                        faker.regexify("[A-Za-z0-9]{150}"),
                        new HashMap<>()
                )
        );
    }

    private static Stream<Arguments> should_generate_token_with_user_id() {

        return Stream.of(
                Arguments.of(
                        clientModelFixture.createRandomOne(false),
                        userIdPrefix + faker.random().nextLong(10),
                        faker.lordOfTheRings().character(),
                        faker.regexify("[A-Za-z0-9]{150}"),
                        new HashMap<>()
                )
        );
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(jwtService);
        Mockito.reset(refreshTokenService);
    }

    @ParameterizedTest
    @MethodSource
    void should_generate_token_without_user_id(ClientModel client,
                                               String accessToken,
                                               Map<String, Object> additionalClaims) {
        // Arrange
        var expiresIn = client.tokenExpiresInMinutes() * 60;
        var signTokenOptions = new SignTokenOptions(null,
                client.clientId(),
                null,
                client.audience(),
                client.issuerUri(),
                expiresIn,
                client.clientSecretDecoded(),
                Map.of());

        when(jwtService.signToken(signTokenOptions)).
                thenReturn(accessToken);

        // Act
        var result = tokenService.generateToken(client, additionalClaims);

        // Assert
        verify(jwtService).signToken(signTokenOptions);
        assertThat(result).isNotBlank();

    }

    @ParameterizedTest
    @MethodSource
    void should_generate_token_with_user_id(ClientModel client,
                                            String userId,
                                            String scope,
                                            String accessToken,
                                            Map<String, Object> additionalClaims) {
        // Arrange
        var expiresIn = client.tokenExpiresInMinutes() * 60;
        var signTokenOptions = new SignTokenOptions(userId,
                client.clientId(),
                scope,
                client.audience(),
                client.issuerUri(),
                expiresIn,
                client.clientSecretDecoded(),
                Map.of());

        when(jwtService.signToken(signTokenOptions)).
                thenReturn(accessToken);

        // Act
        var result = tokenService.generateToken(client, userId, scope, additionalClaims);

        // Assert
        verify(jwtService).signToken(signTokenOptions);
        assertThat(result).isNotBlank();

    }


}