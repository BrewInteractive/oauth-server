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
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        validTokenRequest.setClient_id(client.clientId());
        validTokenRequest.setClient_secret(client.clientSecret());

        var tokenRequestWithoutRefreshToken = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutRefreshToken.setClient_id(client.clientId());
        tokenRequestWithoutRefreshToken.setClient_secret(client.clientSecret());
        tokenRequestWithoutRefreshToken.setRefresh_token("");

        var tokenRequestWithoutClient = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        var authorizationCode = createAuthorizationHeader(client);

        var pair = Pair.of(client.clientId(), client.clientSecret());

        return Stream.of(
                //valid case client credentials from authorization code
                Arguments.of(client,
                        authorizationCode,
                        validTokenRequest,
                        new ValidationResultModel(true, null),
                        pair),
                //valid case client credentials from request model
                Arguments.of(client,
                        "",
                        validTokenRequest,
                        new ValidationResultModel(true, null),
                        pair),
                //valid case client credentials from request model
                Arguments.of(client,
                        null,
                        validTokenRequest,
                        new ValidationResultModel(true, null),
                        pair),
                //invalid case client credentials from authorization code
                Arguments.of(client,
                        authorizationCode,
                        validTokenRequest,
                        new ValidationResultModel(false, "unauthorized_client"),
                        null),
                //no refresh code provided case
                Arguments.of(client,
                        authorizationCode,
                        tokenRequestWithoutRefreshToken,
                        new ValidationResultModel(false, "invalid_request"),
                        pair),
                //no client credentials provided case
                Arguments.of(client,
                        "",
                        tokenRequestWithoutClient,
                        new ValidationResultModel(false, "unauthorized_client"),
                        pair),
                //client not found case
                Arguments.of(null,
                        authorizationCode,
                        validTokenRequest,
                        new ValidationResultModel(false, "unauthorized_client"),
                        pair)
        );
    }

    private static String createAuthorizationHeader(ClientModel client) {
        return Base64.getEncoder().withoutPadding().encodeToString((client.clientId() + ":" + client.clientSecret()).getBytes());
    }

    private static Stream<Arguments> should_generate_token_from_valid_request() {
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.refresh_token});

        var validTokenRequest = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        validTokenRequest.setClient_id(client.clientId());
        validTokenRequest.setClient_secret(client.clientSecret());

        var authorizationCode = createAuthorizationHeader(client);
        var pair = Pair.of(client.clientId(), client.clientSecret());

        var refreshToken = refreshTokenFixture.createRandomOne();
        var accessToken = faker.regexify("[A-Za-z0-9]{150}");
        var idToken = faker.regexify("[A-Za-z0-9]{150}");
        var userIdentityInfo = userIdentityInfoFixture.createRandomOne();
        var firstUserIdentityInfo = userIdentityInfo.entrySet().stream().findFirst().get();
        validTokenRequest.additional_claims.put(firstUserIdentityInfo.getKey(), firstUserIdentityInfo.getValue());

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
                        authorizationCode,
                        validTokenRequest,
                        refreshToken,
                        userIdentityInfo,
                        true,
                        new TokenResultModel(tokenModelWithIdToken, null),
                        pair),
                // While id token is disabled
                Arguments.of(client,
                        authorizationCode,
                        validTokenRequest,
                        refreshToken,
                        null,
                        false,
                        new TokenResultModel(tokenModelWithoutIdToken, null),
                        pair)

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
                                                String authorizationCode,
                                                TokenRequestModel tokenRequest,
                                                ValidationResultModel expectedResult,
                                                Pair<String, String> clientCredentialsPair) {
        // Arrange
        if (!tokenRequest.client_id.isEmpty() && !tokenRequest.client_secret.isEmpty())
            when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                    .thenReturn(clientModel);
        if (!StringUtils.isEmpty(authorizationCode))
            when(clientService.decodeClientCredentials(authorizationCode))
                    .thenReturn(clientCredentialsPair == null ? Optional.empty() : Optional.of(clientCredentialsPair));

        // Act
        var result = tokenGrantProviderRefreshToken.validate(authorizationCode, tokenRequest);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request(ClientModel clientModel,
                                                  String authorizationCode,
                                                  TokenRequestModel tokenRequest,
                                                  RefreshToken refreshToken,
                                                  Map<String, Object> userIdentityInfo,
                                                  Boolean idTokenEnabled,
                                                  TokenResultModel tokenResultModel,
                                                  Pair<String, String> clientCredentialsPair) {

        // Arrange
        var accessToken = tokenResultModel.getResult().getAccessToken();
        when(clientService.getClient(tokenRequest.getClient_id(), tokenRequest.getClient_secret()))
                .thenReturn(clientModel);
        when(clientService.decodeClientCredentials(authorizationCode))
                .thenReturn(clientCredentialsPair == null ? Optional.empty() : Optional.of(clientCredentialsPair));
        when(refreshTokenService.revokeRefreshToken(tokenRequest.getClient_id(), tokenRequest.getRefresh_token(), clientModel.refreshTokenExpiresInDays()))
                .thenReturn(refreshToken);
        when(tokenService.generateToken(clientModel, refreshToken.getClientUser().getUserId(), tokenRequest.getState(), refreshToken.getScope(), tokenRequest.getAdditional_claims()))
                .thenReturn(accessToken);
        when(env.getProperty(eq("id_token.enabled"), anyString()))
                .thenReturn(idTokenEnabled.toString());


        if (idTokenEnabled) {
            var idToken = tokenResultModel.getResult().getIdToken();
            var mergedAdditionalClaims = new HashMap<String, Object>();
            mergedAdditionalClaims.putAll(tokenRequest.getAdditional_claims());
            mergedAdditionalClaims.putAll(userIdentityInfo);

            when(tokenService.generateToken(clientModel, refreshToken.getClientUser().getUserId(), tokenRequest.getState(), refreshToken.getScope(), mergedAdditionalClaims))
                    .thenReturn(idToken);
            when(userIdentityService.getUserIdentityInfo(accessToken))
                    .thenReturn(userIdentityInfo);
        }

        // Act
        var result = tokenGrantProviderRefreshToken.generateToken(authorizationCode, tokenRequest);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(tokenResultModel);
    }


}
