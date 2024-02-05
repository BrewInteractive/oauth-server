package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.service.*;
import com.github.javafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
class TokenGrantProviderAuthorizationCodeTest {
    private static Faker faker;
    private static ClientModelFixture clientModelFixture;
    private static TokenRequestModelFixture tokenRequestModelFixture;
    private static RefreshTokenFixture refreshTokenFixture;
    private static UserIdentityInfoFixture userIdentityInfoFixture;
    private static ActiveAuthorizationCodeFixture activeAuthorizationCodeFixture;
    @Mock
    AuthorizationCodeService authorizationCodeService;
    @Mock
    TokenService tokenService;
    @Mock
    ClientService clientService;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    UserIdentityService userIdentityService;
    @Mock
    Environment env;
    @InjectMocks
    private TokenGrantProviderAuthorizationCode tokenGrantProviderAuthorizationCode;

    private static Stream<Arguments> should_validate_authorization_code_provider() {
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.authorization_code});
        TokenRequestModel validTokenRequest = createValidTokenRequest(client);

        TokenRequestModel tokenRequestWithoutCode = createValidTokenRequest(client);
        tokenRequestWithoutCode.setCode("");

        var tokenRequestWithoutClient = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        var authorizationHeader = createAuthorizationHeader(client);

        var clientCredentialsPair = Pair.of(client.clientId(), client.clientSecret());

        return Stream.of(
                //valid case client credentials from authorization code
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        clientCredentialsPair,
                        new ValidationResultModel(true, null)
                ),
                //valid case client credentials from request model
                Arguments.of(client,
                        "",
                        validTokenRequest,
                        clientCredentialsPair,
                        new ValidationResultModel(true, null)
                ),
                //valid case client credentials from request model
                Arguments.of(client,
                        null,
                        validTokenRequest,
                        clientCredentialsPair,
                        new ValidationResultModel(true, null)
                ),
                //invalid case client credentials from authorization code
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        null,
                        new ValidationResultModel(false, "unauthorized_client")
                ),
                //no auth code provided case
                Arguments.of(client,
                        authorizationHeader,
                        tokenRequestWithoutCode,
                        clientCredentialsPair,
                        new ValidationResultModel(false, "invalid_request")
                ),
                //no client credentials provided case
                Arguments.of(client,
                        "",
                        tokenRequestWithoutClient,
                        clientCredentialsPair,
                        new ValidationResultModel(false, "unauthorized_client")
                ),
                //client not found case
                Arguments.of(null,
                        authorizationHeader,
                        validTokenRequest,
                        clientCredentialsPair,
                        new ValidationResultModel(false, "unauthorized_client")
                )
        );
    }

    private static String createAuthorizationHeader(ClientModel client) {
        return Base64.getEncoder().withoutPadding().encodeToString((client.clientId() + ":" + client.clientSecret()).getBytes());
    }

    private static Stream<Arguments> should_generate_token_from_valid_request_client_issues_refresh_tokens() {
        var clientModel = clientModelFixture.createRandomOne(1, true, new GrantType[]{GrantType.authorization_code});
        TokenRequestModel validTokenRequest = createValidTokenRequest(clientModel);
        var activeAuthorizationCode = createActiveAuthorizationCode(validTokenRequest);

        var refreshToken = refreshTokenFixture.createRandomOne();
        var accessToken = faker.regexify("[A-Za-z0-9]{150}");
        var idToken = faker.regexify("[A-Za-z0-9]{150}");
        var userIdentityInfo = userIdentityInfoFixture.createRandomOne();
        var firstUserIdentityInfo = userIdentityInfo.entrySet().stream().findFirst().get();
        validTokenRequest.additional_claims.put(firstUserIdentityInfo.getKey(), firstUserIdentityInfo.getValue());

        var tokenModelWithIdTokenAndRefreshToken = TokenModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .state(validTokenRequest.getState())
                .expiresIn(clientModel.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .idToken(idToken)
                .build();

        var tokenModelWithRefreshTokenOnly = TokenModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .state(validTokenRequest.getState())
                .expiresIn(clientModel.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .build();


        return Stream.of(

                Arguments.of(clientModel,
                        validTokenRequest,
                        activeAuthorizationCode,
                        refreshToken,
                        userIdentityInfo,
                        true,
                        new TokenResultModel(tokenModelWithIdTokenAndRefreshToken, null)),

                Arguments.of(clientModel,
                        validTokenRequest,
                        activeAuthorizationCode,
                        refreshToken,
                        null,
                        false,
                        new TokenResultModel(tokenModelWithRefreshTokenOnly, null))
        );
    }

    @NotNull
    private static ActiveAuthorizationCode createActiveAuthorizationCode(TokenRequestModel validTokenRequest) {
        var activeAuthorizationCode = activeAuthorizationCodeFixture.createRandomOne();
        activeAuthorizationCode.setCode(validTokenRequest.code);
        activeAuthorizationCode.setRedirectUri(validTokenRequest.redirect_uri);
        return activeAuthorizationCode;
    }

    @NotNull
    private static TokenRequestModel createValidTokenRequest(ClientModel clientModel) {
        var validTokenRequest = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        validTokenRequest.setClient_id(clientModel.clientId());
        validTokenRequest.setClient_secret(clientModel.clientSecret());
        return validTokenRequest;
    }

    private static Stream<Arguments> should_generate_token_from_valid_request_client_not_issues_refresh_tokens() {
        var clientModel = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.authorization_code});
        TokenRequestModel validTokenRequest = createValidTokenRequest(clientModel);

        ActiveAuthorizationCode activeAuthorizationCode = createActiveAuthorizationCode(validTokenRequest);

        var accessToken = faker.regexify("[A-Za-z0-9]{150}");
        var idToken = faker.regexify("[A-Za-z0-9]{150}");
        var userIdentityInfo = userIdentityInfoFixture.createRandomOne();
        var firstUserIdentityInfo = userIdentityInfo.entrySet().stream().findFirst().get();
        validTokenRequest.additional_claims.put(firstUserIdentityInfo.getKey(), firstUserIdentityInfo.getValue());

        var tokenModelWithIdTokenOnly = TokenModel.builder()
                .accessToken(accessToken)
                .state(validTokenRequest.getState())
                .expiresIn(clientModel.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .idToken(idToken)
                .build();


        var tokenModelWithoutIdTokenAndRefreshToken = TokenModel.builder()
                .accessToken(accessToken)
                .state(validTokenRequest.getState())
                .expiresIn(clientModel.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .build();


        return Stream.of(
                Arguments.of(clientModel,
                        validTokenRequest,
                        activeAuthorizationCode,
                        userIdentityInfo,
                        true,
                        new TokenResultModel(tokenModelWithIdTokenOnly, null)),

                Arguments.of(clientModel,
                        validTokenRequest,
                        activeAuthorizationCode,
                        null,
                        false,
                        new TokenResultModel(tokenModelWithoutIdTokenAndRefreshToken, null))
        );
    }

    @BeforeAll
    static void init() {
        faker = new Faker();
        clientModelFixture = new ClientModelFixture();
        tokenRequestModelFixture = new TokenRequestModelFixture();
        refreshTokenFixture = new RefreshTokenFixture();
        userIdentityInfoFixture = new UserIdentityInfoFixture();
        activeAuthorizationCodeFixture = new ActiveAuthorizationCodeFixture();
    }

    @BeforeEach
    void reset() {
        Mockito.reset(clientService);
        Mockito.reset(refreshTokenService);
        Mockito.reset(authorizationCodeService);
        Mockito.reset(tokenService);
        Mockito.reset(userIdentityService);
        Mockito.reset(env);
    }

    @MethodSource
    @ParameterizedTest
    void should_validate_authorization_code_provider(ClientModel clientModel,
                                                     String authorizationHeader,
                                                     TokenRequestModel tokenRequest,
                                                     Pair<String, String> clientCredentialsPair,
                                                     ValidationResultModel expectedResult
    ) {
        // Arrange
        if (!tokenRequest.getClient_id().isEmpty() && !tokenRequest.getClient_secret().isEmpty())
            when(clientService.getClient(tokenRequest.getClient_id(), tokenRequest.getClient_secret()))
                    .thenReturn(clientModel);
        if (!StringUtils.isEmpty(authorizationHeader))
            when(clientService.decodeClientCredentials(authorizationHeader))
                    .thenReturn(clientCredentialsPair == null ? Optional.empty() : Optional.of(clientCredentialsPair));

        // Act
        var result = tokenGrantProviderAuthorizationCode.validate(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request_client_issues_refresh_tokens(ClientModel clientModel,
                                                                               TokenRequestModel tokenRequest,
                                                                               ActiveAuthorizationCode activeAuthorizationCode,
                                                                               RefreshToken refreshToken,
                                                                               Map<String, Object> userIdentityInfo,
                                                                               Boolean idTokenEnabled,
                                                                               TokenResultModel tokenResultModel
    ) {
        // Arrange
        var accessToken = tokenResultModel.getResult().getAccessToken();
        String authorizationHeader = createAuthorizationHeader(clientModel);
        var clientCredentialsPair = Pair.of(clientModel.clientId(), clientModel.clientSecret());


        when(clientService.getClient(tokenRequest.getClient_id(), tokenRequest.getClient_secret()))
                .thenReturn(clientModel);
        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(Optional.of(clientCredentialsPair));
        when(authorizationCodeService.getAuthorizationCode(tokenRequest.getCode(), tokenRequest.getRedirect_uri(), true))
                .thenReturn(activeAuthorizationCode);
        when(tokenService.generateToken(clientModel, activeAuthorizationCode.getClientUser().getUserId(), tokenRequest.getState(), tokenRequest.getAdditional_claims()))
                .thenReturn(accessToken);
        when(env.getProperty(eq("id_token.enabled"), anyString()))
                .thenReturn(idTokenEnabled.toString());
        when(refreshTokenService.createRefreshToken(tokenRequest.getClient_id(), activeAuthorizationCode.getClientUser().getUserId(), clientModel.refreshTokenExpiresInDays()))
                .thenReturn(refreshToken);

        if (idTokenEnabled) {
            var idToken = tokenResultModel.getResult().getIdToken();
            var mergedAdditionalClaims = new HashMap<String, Object>();
            mergedAdditionalClaims.putAll(tokenRequest.getAdditional_claims());
            mergedAdditionalClaims.putAll(userIdentityInfo);

            when(tokenService.generateToken(clientModel, activeAuthorizationCode.getClientUser().getUserId(), tokenRequest.getState(), mergedAdditionalClaims))
                    .thenReturn(idToken);
            when(userIdentityService.getUserIdentityInfo(accessToken))
                    .thenReturn(userIdentityInfo);
        }

        // Act
        var result = tokenGrantProviderAuthorizationCode.generateToken(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(tokenResultModel);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request_client_not_issues_refresh_tokens(ClientModel clientModel,
                                                                                   TokenRequestModel tokenRequest,
                                                                                   ActiveAuthorizationCode activeAuthorizationCode,
                                                                                   Map<String, Object> userIdentityInfo,
                                                                                   Boolean idTokenEnabled,
                                                                                   TokenResultModel tokenResultModel
    ) {
        // Arrange
        var accessToken = tokenResultModel.getResult().getAccessToken();
        String authorizationHeader = createAuthorizationHeader(clientModel);
        var clientCredentialsPair = Pair.of(clientModel.clientId(), clientModel.clientSecret());

        when(clientService.getClient(tokenRequest.getClient_id(), tokenRequest.getClient_secret()))
                .thenReturn(clientModel);
        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(Optional.of(clientCredentialsPair));
        when(authorizationCodeService.getAuthorizationCode(tokenRequest.getCode(), tokenRequest.getRedirect_uri(), true))
                .thenReturn(activeAuthorizationCode);
        when(tokenService.generateToken(clientModel, activeAuthorizationCode.getClientUser().getUserId(), tokenRequest.getState(), tokenRequest.getAdditional_claims()))
                .thenReturn(accessToken);
        when(env.getProperty(eq("id_token.enabled"), anyString()))
                .thenReturn(idTokenEnabled.toString());

        if (idTokenEnabled) {
            var idToken = tokenResultModel.getResult().getIdToken();
            var mergedAdditionalClaims = new HashMap<String, Object>();
            mergedAdditionalClaims.putAll(tokenRequest.getAdditional_claims());
            mergedAdditionalClaims.putAll(userIdentityInfo);

            when(tokenService.generateToken(clientModel, activeAuthorizationCode.getClientUser().getUserId(), tokenRequest.getState(), mergedAdditionalClaims))
                    .thenReturn(idToken);
            when(userIdentityService.getUserIdentityInfo(accessToken))
                    .thenReturn(userIdentityInfo);
        }

        // Act
        var result = tokenGrantProviderAuthorizationCode.generateToken(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(tokenResultModel);
    }

}
