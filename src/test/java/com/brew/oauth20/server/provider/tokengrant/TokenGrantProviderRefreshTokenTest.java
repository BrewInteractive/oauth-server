package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientAuthenticationFailedException;
import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.service.*;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TokenGrantProviderRefreshTokenTest {
    private static Faker faker;
    private static ClientModelFixture clientModelFixture;
    private static CustomClaimFixture customClaimFixture;
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
    CustomClaimService customClaimService;
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
                        clientCredentials,
                        true,
                        null),
                //valid case client credentials from request model
                Arguments.of(client,
                        validTokenRequest,
                        clientCredentials,
                        true,
                        null),
                //valid case client credentials from request model
                Arguments.of(client,
                        validTokenRequest,
                        clientCredentials,
                        true,
                        null),
                //no refresh code provided case
                Arguments.of(client,
                        tokenRequestWithoutRefreshToken,
                        clientCredentials,
                        null,
                        new OAuthException(OAuthError.INVALID_REQUEST)),
                //no client credentials provided case
                Arguments.of(client,
                        tokenRequestWithoutClient,
                        clientCredentials,
                        null,
                        new ClientAuthenticationFailedException()),
                //client not found case
                Arguments.of(null,
                        validTokenRequest,
                        clientCredentials,
                        null,
                        new ClientAuthenticationFailedException())
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
        var customClaims = customClaimFixture.createRandomOne();
        customClaims.put(firstUserIdentityInfo.getKey(), firstUserIdentityInfo.getValue());

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
                        customClaims,
                        true,
                        tokenModelWithIdToken,
                        clientCredentials),
                // While id token is disabled
                Arguments.of(client,
                        validTokenRequest,
                        refreshToken,
                        null,
                        customClaims,
                        false,
                        tokenModelWithoutIdToken,
                        clientCredentials)

        );
    }

    @BeforeAll
    static void init() {
        faker = new Faker();
        clientModelFixture = new ClientModelFixture();
        customClaimFixture = new CustomClaimFixture();
        tokenRequestModelFixture = new TokenRequestModelFixture();
        refreshTokenFixture = new RefreshTokenFixture();
        userIdentityInfoFixture = new UserIdentityInfoFixture();

    }

    @BeforeEach
    void reset() {
        Mockito.reset(clientService);
        Mockito.reset(customClaimService);
        Mockito.reset(refreshTokenService);
        Mockito.reset(tokenService);
        Mockito.reset(userIdentityService);
        Mockito.reset(env);
    }

    @MethodSource
    @ParameterizedTest
    void should_validate_refresh_token_provider(ClientModel clientModel,
                                                TokenRequestModel tokenRequest,
                                                ClientCredentialsModel clientCredentialsModel,
                                                Boolean expectedResult,
                                                Exception expectedException) {
        // Arrange
        if (!tokenRequest.getClientId().isEmpty() && !tokenRequest.getClientSecret().isEmpty())
            when(clientService.getClient(tokenRequest.getClientId(), tokenRequest.getClientSecret()))
                    .thenReturn(clientModel);

        // Act && Assert
        if (expectedResult != null) {
            var actualResult = tokenGrantProviderRefreshToken.validate(clientCredentialsModel, tokenRequest);
            assertThat(actualResult).isEqualTo(expectedResult);
        }
        if (expectedException != null) {
            assertThatThrownBy(() -> tokenGrantProviderRefreshToken.validate(clientCredentialsModel, tokenRequest))
                    .isInstanceOf(expectedException.getClass())
                    .hasMessage(expectedException.getMessage());
        }

    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request(ClientModel clientModel,
                                                  TokenRequestModel tokenRequest,
                                                  RefreshToken refreshToken,
                                                  Map<String, Object> userIdentityInfo,
                                                  Map<String, Object> customClaims,
                                                  Boolean idTokenEnabled,
                                                  TokenModel tokenModel,
                                                  ClientCredentialsModel clientCredentialsModel) {

        // Arrange
        var accessToken = tokenModel.getAccessToken();
        var userId = refreshToken.getClientUser().getUserId();
        when(clientService.getClient(tokenRequest.getClientId(), tokenRequest.getClientSecret()))
                .thenReturn(clientModel);
        when(customClaimService.getCustomClaims(any(HookModel.class), eq(userId)))
                .thenReturn(customClaims);
        when(refreshTokenService.revokeRefreshToken(tokenRequest.getClientId(), tokenRequest.getRefreshToken(), clientModel.refreshTokenExpiresInDays()))
                .thenReturn(refreshToken);
        when(tokenService.generateToken(clientModel, userId, refreshToken.getScope(), customClaims))
                .thenReturn(accessToken);
        when(env.getProperty(eq("id_token.enabled"), anyString()))
                .thenReturn(idTokenEnabled.toString());


        if (idTokenEnabled) {
            var idToken = tokenModel.getIdToken();
            var mergedAdditionalClaims = new HashMap<String, Object>();
            mergedAdditionalClaims.putAll(customClaims);
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
