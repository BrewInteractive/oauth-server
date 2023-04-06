package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.TokenRequestFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TokenGrantProviderRefreshTokenTest {
    @MockBean
    RefreshTokenService refreshTokenService;
    @MockBean
    TokenService tokenService;
    @MockBean
    private ClientService clientService;


    private static Stream<Arguments> should_return_valid_result() {

        var clientModelFixture = new ClientModelFixture();
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.refresh_token});
        var tokenRequestFixture = new TokenRequestFixture();

        var validTokenRequest = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        validTokenRequest.setClient_id(client.clientId());
        validTokenRequest.setClient_secret(client.clientSecret());

        var tokenRequestWithoutRefreshToken = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutRefreshToken.setClient_id(client.clientId());
        tokenRequestWithoutRefreshToken.setClient_secret(client.clientSecret());
        tokenRequestWithoutRefreshToken.setRefresh_token("");

        var tokenRequestWithoutClient = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        String authorizationCode = client.clientIdClientSecretEncoded();

        var pair = Optional.of(Pair.of(client.clientId(), client.clientSecret()));

        return Stream.of(
                //valid case client credentials from authorizationcode
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
                //invalid case client credentials from authorizationcode
                Arguments.of(client,
                        authorizationCode,
                        validTokenRequest,
                        new ValidationResultModel(false, "unauthorized_client"),
                        Optional.empty()),
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


    @MethodSource
    @ParameterizedTest
    void should_return_valid_result(ClientModel clientModel,
                                    String authorizationCode,
                                    TokenRequestModel tokenRequest,
                                    ValidationResultModel expectedResult,
                                    Optional<Pair<String, String>> clientCredentialsPair) {
        // Arrange
        Mockito.reset(clientService);
        Mockito.reset(refreshTokenService);
        if (!tokenRequest.client_id.isEmpty() && !tokenRequest.client_secret.isEmpty())
            when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                    .thenReturn(clientModel);
        if (!authorizationCode.isEmpty())
            when(clientService.decodeClientCredentials(authorizationCode))
                    .thenReturn(clientCredentialsPair);

        var provider = new TokenGrantProviderRefreshToken(
                clientService,
                refreshTokenService,
                tokenService
        );

        // Arrange
        var result = provider.validate(authorizationCode, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }
}
