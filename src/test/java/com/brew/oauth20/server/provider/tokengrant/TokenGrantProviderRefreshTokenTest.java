package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.RefreshTokenFixture;
import com.brew.oauth20.server.fixture.TokenRequestModelFixture;
import com.brew.oauth20.server.fixture.UserIdentityInfoFixture;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.service.UserIdentityService;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TokenGrantProviderRefreshTokenTest {
    private static Faker faker;
    private static ClientModelFixture clientModelFixture;
    private static TokenRequestModelFixture tokenRequestModelFixture;
    private static RefreshTokenFixture refreshTokenFixture;
    private static UserIdentityInfoFixture userIdentityInfoFixture;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    TokenService tokenService;
    @Mock
    ClientService clientService;
    @Mock
    UserIdentityService userIdentityService;
    @Mock
    Environment env;
    @InjectMocks
    private TokenGrantProviderRefreshToken tokenGrantProviderRefreshToken;

    private static Stream<Arguments> should_validate_refresh_token_provider() {
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.refresh_token});

        var validTokenRequest = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        validTokenRequest.setClientId(client.clientId());
        validTokenRequest.setClientSecret(client.clientSecret());

        var tokenRequestWithoutRefreshToken = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutRefreshToken.setClientId(client.clientId());
        tokenRequestWithoutRefreshToken.setClientSecret(client.clientSecret());
        tokenRequestWithoutRefreshToken.setRefreshToken("");

        var tokenRequestWithoutClient = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutClient.setClientId("");
        tokenRequestWithoutClient.setClientSecret("");

        var clientCredentials = new ClientCredentialsModel(client.clientId(), client.clientSecret());

        return Stream.of(
                //valid case client credentials from authorization code
                Arguments.of(client,
                        validTokenRequest,
                        true,
                        clientCredentials),
                //valid case client credentials from request model
                Arguments.of(client,
                        validTokenRequest,
                        true,
                        clientCredentials),
                //valid case client credentials from request model
                Arguments.of(client,
                        validTokenRequest,
                        true,
                        clientCredentials),
                //invalid case client credentials from authorization code
                Arguments.of(client,
                        validTokenRequest,
                        new ValidationResultModel(false, "unauthorized_client"),
                        null),
                //no refresh code provided case
                Arguments.of(client,
                        tokenRequestWithoutRefreshToken,
                        new ValidationResultModel(false, "invalid_request"),
                        clientCredentials),
                //no client credentials provided case
                Arguments.of(client,
                        tokenRequestWithoutClient,
                        new ValidationResultModel(false, "unauthorized_client"),
                        clientCredentials),
                //client not found case
                Arguments.of(null,
                        validTokenRequest,
                        new ValidationResultModel(false, "unauthorized_client"),
                        clientCredentials)
        );
    }

    private static Stream<Arguments> should_generate_token_from_valid_request() {
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.refresh_token});

        var validTokenRequest = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        validTokenRequest.setClientId(client.clientId());
        validTokenRequest.setClientSecret(client.clientSecret());

        var clientCredentials = new ClientCredentialsModel(client.clientId(), client.clientSecret());

        var refreshToken = refreshTokenFixture.createRandomOne();
        var accessToken = faker.regexify("[A-Za-z0-9]{150}");
        var idToken = faker.regexify("[A-Za-z0-9]{150}");
        var userIdentityInfo = userIdentityInfoFixture.createRandomOne();
        var firstUserIdentityInfo = userIdentityInfo.entrySet().stream().findFirst().get();
        validTokenRequest.getAdditionalClaims().put(firstUserIdentityInfo.getKey(), firstUserIdentityInfo.getValue());

        var tokenModelWithIdToken = TokenModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .state(validTokenRequest.getState())
                .expiresIn(client.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .idToken(idToken)
                .build();

        var tokenModelWithoutIdToken = TokenModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .state(validTokenRequest.getState())
                .expiresIn(client.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .build();

        return Stream.of(
                // While id token is enabled
                Arguments.of(client,
                        validTokenRequest,
                        refreshToken,
                        userIdentityInfo,
                        true,
                        tokenModelWithIdToken,
                        clientCredentials),
                // While id token is disabled
                Arguments.of(client,
                        validTokenRequest,
                        refreshToken,
                        null,
                        false,
                        tokenModelWithoutIdToken,
                        clientCredentials)

        );
    }

    @BeforeAll
    static void init() {
        faker = new Faker();
        clientModelFixture = new ClientModelFixture();
        tokenRequestModelFixture = new TokenRequestModelFixture();
        refreshTokenFixture = new RefreshTokenFixture();
        userIdentityInfoFixture = new UserIdentityInfoFixture();

    }

    @BeforeEach
    void reset() {
        Mockito.reset(clientService);
        Mockito.reset(refreshTokenService);
        Mockito.reset(tokenService);
        Mockito.reset(userIdentityService);
        Mockito.reset(env);
    }

    @MethodSource
    @ParameterizedTest
    void should_validate_refresh_token_provider(ClientModel clientModel,
                                                TokenRequestModel tokenRequest,
                                                Boolean expectedResult,
                                                ClientCredentialsModel clientCredentialsModel) {
        // Arrange
        if (!tokenRequest.getClientId().isEmpty() && !tokenRequest.getClientSecret().isEmpty())
            when(clientService.getClient(tokenRequest.getClientId(), tokenRequest.getClientSecret()))
                    .thenReturn(clientModel);

        // Act
        var result = tokenGrantProviderRefreshToken.validate(clientCredentialsModel, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request(ClientModel clientModel,
                                                  TokenRequestModel tokenRequest,
                                                  RefreshToken refreshToken,
                                                  Map<String, Object> userIdentityInfo,
                                                  Boolean idTokenEnabled,
                                                  TokenModel tokenModel,
                                                  ClientCredentialsModel clientCredentialsModel) {

        // Arrange
        var accessToken = tokenModel.getAccessToken();
        when(clientService.getClient(tokenRequest.getClientId(), tokenRequest.getClientSecret()))
                .thenReturn(clientModel);

        when(refreshTokenService.revokeRefreshToken(tokenRequest.getClientId(), tokenRequest.getRefreshToken(), clientModel.refreshTokenExpiresInDays()))
                .thenReturn(refreshToken);
        when(tokenService.generateToken(clientModel, refreshToken.getClientUser().getUserId(), refreshToken.getScope(), tokenRequest.getAdditionalClaims()))
                .thenReturn(accessToken);
        when(env.getProperty(eq("id_token.enabled"), anyString()))
                .thenReturn(idTokenEnabled.toString());


        if (idTokenEnabled) {
            var idToken = tokenModel.getIdToken();
            var mergedAdditionalClaims = new HashMap<String, Object>();
            mergedAdditionalClaims.putAll(tokenRequest.getAdditionalClaims());
            mergedAdditionalClaims.putAll(userIdentityInfo);

            when(tokenService.generateToken(clientModel, refreshToken.getClientUser().getUserId(), refreshToken.getScope(), mergedAdditionalClaims))
                    .thenReturn(idToken);
            when(userIdentityService.getUserIdentityInfo(accessToken))
                    .thenReturn(userIdentityInfo);
        }

        // Act
        var result = tokenGrantProviderRefreshToken.generateToken(clientCredentialsModel, tokenRequest);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(tokenModel);
    }
}
