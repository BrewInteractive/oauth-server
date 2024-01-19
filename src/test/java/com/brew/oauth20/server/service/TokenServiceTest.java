package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.RefreshTokenFixture;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TokenServiceTest {

    private static ClientModelFixture clientModelFixture;
    private static RefreshTokenFixture refreshTokenFixture;
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
        refreshTokenFixture = new RefreshTokenFixture();

    }

    private static Stream<Arguments> should_generate_token_without_user_id() {

        return Stream.of(
                Arguments.of(
                        clientModelFixture.createRandomOne(false),
                        faker.lordOfTheRings().location(),
                        faker.regexify("[A-Za-z0-9]{150}"),
                        "Bearer",
                        new HashMap<>()
                )
        );
    }

    private static Stream<Arguments> should_generate_token_without_refresh_token() {

        return Stream.of(
                Arguments.of(
                        clientModelFixture.createRandomOne(false),
                        faker.letterify("?").repeat(20),
                        faker.lordOfTheRings().location(),
                        faker.regexify("[A-Za-z0-9]{150}"),
                        "Bearer",
                        new HashMap<>()
                )
        );
    }

    private static Stream<Arguments> should_generate_token_with_refresh_token() {

        return Stream.of(
                Arguments.of(
                        clientModelFixture.createRandomOne(true),
                        faker.letterify("?").repeat(20),
                        faker.lordOfTheRings().location(),
                        faker.regexify("[A-Za-z0-9]{150}"),
                        refreshTokenFixture.createRandomOne(),
                        "Bearer",
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
                                               String state,
                                               String accessToken,
                                               String tokenType,
                                               Map<String, Object> additionalClaims) {
        // Arrange
        var expiresIn = client.tokenExpiresInMinutes() * 60;
        var signTokenOptions = new SignTokenOptions(null,
                client.audience(),
                client.issuerUri(),
                expiresIn,
                client.clientSecretDecoded(),
                Map.of());

        when(jwtService.signToken(signTokenOptions)).
                thenReturn(accessToken);

        // Act
        var result = tokenService.generateToken(client, state, additionalClaims);

        // Assert
        assertThat(result.getTokenType()).isEqualTo(tokenType);
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(result.getRefreshToken()).isBlank();

    }

    @ParameterizedTest
    @MethodSource
    void should_generate_token_without_refresh_token(ClientModel client,
                                                     String userId,
                                                     String state,
                                                     String accessToken,
                                                     String tokenType,
                                                     Map<String, Object> additionalClaims) {
        // Arrange
        var expiresIn = client.tokenExpiresInMinutes() * 60;
        var signTokenOptions = new SignTokenOptions(userId,
                client.audience(),
                client.issuerUri(),
                expiresIn,
                client.clientSecretDecoded(),
                additionalClaims);

        when(jwtService.signToken(signTokenOptions)).
                thenReturn(accessToken);

        // Act
        var result = tokenService.generateToken(client, userId, state, additionalClaims, null);

        // Assert
        assertThat(result.getTokenType()).isEqualTo(tokenType);
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(result.getRefreshToken()).isBlank();

    }

    @ParameterizedTest
    @MethodSource
    void should_generate_token_with_refresh_token(ClientModel client,
                                                  String userId,
                                                  String state,
                                                  String accessToken,
                                                  RefreshToken refreshToken,
                                                  String tokenType,
                                                  Map<String, Object> additionalClaims) {
        // Arrange
        var expiresIn = client.tokenExpiresInMinutes() * 60;
        var signTokenOptions = new SignTokenOptions(userId,
                client.audience(),
                client.issuerUri(),
                expiresIn,
                client.clientSecretDecoded(),
                additionalClaims);

        when(jwtService.signToken(signTokenOptions)).
                thenReturn(accessToken);


        // Act
        var result = tokenService.generateToken(client, userId, state, additionalClaims, refreshToken.getToken());

        // Assert
        assertThat(result.getTokenType()).isEqualTo(tokenType);
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken.getToken());
    }
}