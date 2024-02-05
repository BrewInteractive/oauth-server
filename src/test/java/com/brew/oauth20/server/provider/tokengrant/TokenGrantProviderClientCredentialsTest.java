package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.TokenRequestModelFixture;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.TokenService;
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
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TokenGrantProviderClientCredentialsTest {

    private static Faker faker;
    private static ClientModelFixture clientModelFixture;
    private static TokenRequestModelFixture tokenRequestModelFixture;

    @Mock
    TokenService tokenService;
    @Mock
    ClientService clientService;

    @InjectMocks
    private TokenGrantProviderClientCredentials tokenGrantProviderClientCredentials;

    private static Stream<Arguments> should_generate_token_from_valid_request() {
        var clientModel = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.client_credentials});

        TokenRequestModel validTokenRequest = createValidTokenRequest(clientModel);

        var accessToken = faker.regexify("[A-Za-z0-9]{150}");

        var tokenModel = TokenModel.builder()
                .accessToken(accessToken)
                .state(validTokenRequest.getState())
                .expiresIn(clientModel.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .build();

        return Stream.of(
                Arguments.of(clientModel,
                        validTokenRequest,
                        new TokenResultModel(tokenModel, null))

        );
    }

    @NotNull
    private static TokenRequestModel createValidTokenRequest(ClientModel clientModel) {
        var validTokenRequest = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
        validTokenRequest.setClient_id(clientModel.clientId());
        validTokenRequest.setClient_secret(clientModel.clientSecret());
        return validTokenRequest;
    }

    private static String createAuthorizationHeader(ClientModel client) {
        return Base64.getEncoder().withoutPadding().encodeToString((client.clientId() + ":" + client.clientSecret()).getBytes());
    }

    private static Stream<Arguments> should_validate_client_credentials_provider() {
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.client_credentials});
        TokenRequestModel validTokenRequest = createValidTokenRequest(client);

        var tokenRequestWithoutClient = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
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


    @BeforeAll
    static void init() {
        faker = new Faker();
        clientModelFixture = new ClientModelFixture();
        tokenRequestModelFixture = new TokenRequestModelFixture();
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(clientService);
        Mockito.reset(tokenService);
    }

    @MethodSource
    @ParameterizedTest
    void should_validate_client_credentials_provider(ClientModel clientModel,
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
        var result = tokenGrantProviderClientCredentials.validate(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request(ClientModel clientModel,
                                                  TokenRequestModel tokenRequestModel,
                                                  TokenResultModel tokenResultModel) {

        // Arrange
        var accessToken = tokenResultModel.getResult().getAccessToken();
        var authorizationHeader = Base64.getEncoder().withoutPadding().encodeToString((clientModel.clientId() + ":" + clientModel.clientSecret()).getBytes());
        var clientCredentialsPair = Pair.of(clientModel.clientId(), clientModel.clientSecret());


        when(clientService.getClient(tokenRequestModel.getClient_id(), tokenRequestModel.getClient_secret()))
                .thenReturn(clientModel);

        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(Optional.of(clientCredentialsPair));

        when(tokenService.generateToken(clientModel, tokenRequestModel.getState(), tokenRequestModel.getAdditional_claims()))
                .thenReturn(accessToken);

        // Act
        var result = tokenGrantProviderClientCredentials.generateToken(authorizationHeader, tokenRequestModel);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(tokenResultModel);
    }
}
