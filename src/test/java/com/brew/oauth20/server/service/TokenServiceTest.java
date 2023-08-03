package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.RefreshTokenFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.model.TokenModel;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
                        faker.random().nextLong(Long.MAX_VALUE),
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
                        faker.random().nextLong(Long.MAX_VALUE),
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
                        faker.random().nextLong(Long.MAX_VALUE),
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
                                               long expiresIn,
                                               String tokenType,
                                               Map<String, Object> additionalClaims) {
        // Arrange
        var token = TokenModel.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .state(state)
                .tokenType(tokenType)
                .build();

        var signTokenOptions = new SignTokenOptions(null,
                client.audience(),
                client.issuerUri(),
                state,
                client.tokenExpiresInMinutes(),
                Map.of());

        when(jwtService.signToken(signTokenOptions)).
                thenReturn(token);

        // Act
        var result = tokenService.generateToken(client, state, additionalClaims);

        // Assert
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isPositive();
        assertThat(result.getRefreshToken()).isBlank();

    }

    @ParameterizedTest
    @MethodSource
    void should_generate_token_without_refresh_token(ClientModel client,
                                                     String userId,
                                                     String state,
                                                     String accessToken,
                                                     long expiresIn,
                                                     String tokenType,
                                                     Map<String, Object> additionalClaims) {
        // Arrange
        var token = TokenModel.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .state(state)
                .tokenType(tokenType)
                .build();

        var signTokenOptions = new SignTokenOptions(userId,
                client.audience(),
                client.issuerUri(),
                state,
                client.tokenExpiresInMinutes(),
                Map.of());

        when(jwtService.signToken(signTokenOptions)).
                thenReturn(token);

        // Act
        var result = tokenService.generateToken(client, userId, state, additionalClaims);

        // Assert
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isPositive();
        assertThat(result.getRefreshToken()).isBlank();

    }

    @ParameterizedTest
    @MethodSource
    void should_generate_token_with_refresh_token(ClientModel client,
                                                  String userId,
                                                  String state,
                                                  String accessToken,
                                                  long expiresIn,
                                                  RefreshToken refreshToken,
                                                  String tokenType,
                                                  Map<String, Object> additionalClaims) {
        // Arrange
        var token = TokenModel.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .refreshToken(refreshToken.getToken())
                .state(state)
                .tokenType(tokenType)
                .build();

        var signTokenOptions = new SignTokenOptions(userId,
                client.audience(),
                client.issuerUri(),
                state,
                client.tokenExpiresInMinutes(),
                Map.of());

        when(jwtService.signToken(signTokenOptions, refreshToken.getToken())).
                thenReturn(token);

        when(refreshTokenService.createRefreshToken(eq(client.clientId()), eq(userId), anyString(), eq(client.refreshTokenExpiresInDays()))).
                thenReturn(refreshToken);


        // Act
        var result = tokenService.generateToken(client, userId, state, additionalClaims);

        // Assert
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getAccessToken().length()).isBetween(100, 1000);
        assertThat(result.getExpiresIn()).isPositive();
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken.getToken());
    }
}