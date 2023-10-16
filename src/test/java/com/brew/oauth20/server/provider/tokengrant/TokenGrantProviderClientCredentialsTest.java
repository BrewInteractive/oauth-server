package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientsUserNotFoundException;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.TokenRequestModelFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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
    @Mock
    TokenService tokenService;
    @Mock
    ClientService clientService;

    @InjectMocks
    private TokenGrantProviderClientCredentials tokenGrantProviderClientCredentials;

    private static Stream<Arguments> should_generate_token_from_valid_request() {
        var clientModelFixture = new ClientModelFixture();

        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.client_credentials});

        var tokenRequestFixture = new TokenRequestModelFixture();

        var validTokenRequest = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
        validTokenRequest.setClient_id(client.clientId());
        validTokenRequest.setClient_secret(client.clientSecret());

        var tokenRequestWithoutClient = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        String authorizationHeader = Base64.getEncoder().withoutPadding().encodeToString((client.clientId() + ":" + client.clientSecret()).getBytes());

        var clientCredentials = Pair.of(client.clientId(), client.clientSecret());

        var tokenModel = TokenModel.builder().build();

        return Stream.of(
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        new TokenResultModel(tokenModel, null),
                        clientCredentials),
                Arguments.of(client,
                        authorizationHeader,
                        tokenRequestWithoutClient,
                        new TokenResultModel(null, "unauthorized_client"),
                        clientCredentials),
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        new TokenResultModel(null, "unauthorized_client"),
                        null)
        );
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(clientService);
        Mockito.reset(tokenService);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request(ClientModel clientModel,
                                                  String authorizationHeader,
                                                  TokenRequestModel tokenRequest,
                                                  TokenResultModel tokenResultModel,
                                                  Pair<String, String> clientCredentialsPair) {

        // Arrange
        when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                .thenReturn(clientModel);

        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(clientCredentialsPair == null ? Optional.empty() : Optional.of(clientCredentialsPair));

        when(tokenService.generateToken(clientModel, tokenRequest.state, tokenRequest.additional_claims))
                .thenReturn(tokenResultModel.getResult());

        // Act
        var result = tokenGrantProviderClientCredentials.generateToken(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(tokenResultModel);
    }

    @Test
    void should_return_unauthorized_client_clients_user_not_found_test() {
        // Arrange
        var clientModelFixture = new ClientModelFixture();

        var clientModel = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.client_credentials});

        var tokenRequestFixture = new TokenRequestModelFixture();

        var tokenRequest = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
        tokenRequest.setClient_id(clientModel.clientId());
        tokenRequest.setClient_secret(clientModel.clientSecret());

        var tokenRequestWithoutClient = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.client_credentials});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        String authorizationHeader = Base64.getEncoder().withoutPadding().encodeToString((clientModel.clientId() + ":" + clientModel.clientSecret()).getBytes());

        var clientCredentialsPair = Pair.of(clientModel.clientId(), clientModel.clientSecret());

        when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                .thenReturn(clientModel);

        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(Optional.of(clientCredentialsPair));

        when(tokenService.generateToken(clientModel, tokenRequest.state, tokenRequest.additional_claims))
                .thenThrow(new ClientsUserNotFoundException(clientModel.clientId(), "user"));

        var tokenResultModel = new TokenResultModel(null, "unauthorized_client");
        // Act
        var result = tokenGrantProviderClientCredentials.generateToken(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(tokenResultModel);
    }
}
