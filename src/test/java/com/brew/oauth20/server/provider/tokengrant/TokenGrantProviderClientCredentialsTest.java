package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.TokenRequestModelFixture;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.TokenService;
import com.github.javafaker.Faker;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.client_credentials});

        TokenRequestModel validTokenRequest = createValidTokenRequest(client);

        var accessToken = faker.regexify("[A-Za-z0-9]{150}");

        var tokenModel = TokenModel.builder()
                .accessToken(accessToken)
                .state(validTokenRequest.getState())
                .expiresIn(client.tokenExpiresInSeconds())
                .tokenType("Bearer")
                .build();

        return Stream.of(
                Arguments.of(client,
                        validTokenRequest,
                        tokenModel)

        );
    }

    @NotNull
    private static TokenRequestModel createValidTokenRequest(ClientModel clientModel) {
        var validTokenRequest = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
        validTokenRequest.setClientId(clientModel.clientId());
        validTokenRequest.setClientSecret(clientModel.clientSecret());
        return validTokenRequest;
    }

    private static Stream<Arguments> should_validate_client_credentials_provider() {
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.client_credentials});
        TokenRequestModel validTokenRequest = createValidTokenRequest(client);

        var tokenRequestWithoutClient = tokenRequestModelFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
        tokenRequestWithoutClient.setClientId("");
        tokenRequestWithoutClient.setClientSecret("");


        var clientCredentials = new ClientCredentialsModel(client.clientId(), client.clientSecret());

        return Stream.of(
                //valid case client credentials from authorization code
                Arguments.of(client,
                        validTokenRequest,
                        clientCredentials,
                        new ValidationResultModel(true, null)
                ),
                //valid case client credentials from request model
                Arguments.of(client,
                        validTokenRequest,
                        clientCredentials,
                        new ValidationResultModel(true, null)
                ),
                //valid case client credentials from request model
                Arguments.of(client,
                        validTokenRequest,
                        clientCredentials,
                        new ValidationResultModel(true, null)
                ),
                //invalid case client credentials from authorization code
                Arguments.of(client,
                        validTokenRequest,
                        null,
                        new ValidationResultModel(false, "unauthorized_client")
                ),
                //no client credentials provided case
                Arguments.of(client,
                        tokenRequestWithoutClient,
                        clientCredentials,
                        new ValidationResultModel(false, "unauthorized_client")
                ),
                //client not found case
                Arguments.of(null,
                        validTokenRequest,
                        clientCredentials,
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
                                                     TokenRequestModel tokenRequest,
                                                     ClientCredentialsModel clientCredentialsModel,
                                                     Boolean expectedResult
    ) {
        // Arrange
        if (!tokenRequest.getClientId().isEmpty() && !tokenRequest.getClientSecret().isEmpty())
            when(clientService.getClient(tokenRequest.getClientId(), tokenRequest.getClientSecret()))
                    .thenReturn(clientModel);

        // Act
        var result = tokenGrantProviderClientCredentials.validate(clientCredentialsModel, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request(ClientModel clientModel,
                                                  TokenRequestModel tokenRequestModel,
                                                  TokenModel tokenModel) {

        // Arrange
        var accessToken = tokenModel.getAccessToken();
        var clientCredentialsModel = new ClientCredentialsModel(clientModel.clientId(), clientModel.clientSecret());


        when(clientService.getClient(tokenRequestModel.getClientId(), tokenRequestModel.getClientSecret()))
                .thenReturn(clientModel);

        when(tokenService.generateToken(clientModel, tokenRequestModel.getAdditionalClaims()))
                .thenReturn(accessToken);

        // Act
        var result = tokenGrantProviderClientCredentials.generateToken(clientCredentialsModel, tokenRequestModel);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(tokenModel);
    }
}
