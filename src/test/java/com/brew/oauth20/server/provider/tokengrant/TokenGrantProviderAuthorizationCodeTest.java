package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.AuthorizationCode;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.AuthorizationCodeFixture;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.TokenRequestModelFixture;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
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
class TokenGrantProviderAuthorizationCodeTest {
    @Mock
    AuthorizationCodeService authorizationCodeService;
    @Mock
    TokenService tokenService;
    @Mock
    ClientService clientService;

    @InjectMocks
    private TokenGrantProviderAuthorizationCode tokenGrantProviderAuthorizationCode;

    private static Stream<Arguments> should_validate_authorization_code_provider() {

        var clientModelFixture = new ClientModelFixture();
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.refresh_token});
        var tokenRequestFixture = new TokenRequestModelFixture();

        var validTokenRequest = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        validTokenRequest.setClient_id(client.clientId());
        validTokenRequest.setClient_secret(client.clientSecret());

        var tokenRequestWithoutCode = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutCode.setClient_id(client.clientId());
        tokenRequestWithoutCode.setClient_secret(client.clientSecret());
        tokenRequestWithoutCode.setCode("");

        var tokenRequestWithoutClient = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.refresh_token});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        String authorizationHeader = Base64.getEncoder().withoutPadding().encodeToString((client.clientId() + ":" + client.clientSecret()).getBytes());

        var pair = Pair.of(client.clientId(), client.clientSecret());

        return Stream.of(
                //valid case client credentials from authorization code
                Arguments.of(client,
                        authorizationHeader,
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
                        authorizationHeader,
                        validTokenRequest,
                        new ValidationResultModel(false, "unauthorized_client"),
                        null),
                //no auth code provided case
                Arguments.of(client,
                        authorizationHeader,
                        tokenRequestWithoutCode,
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
                        authorizationHeader,
                        validTokenRequest,
                        new ValidationResultModel(false, "unauthorized_client"),
                        pair)
        );
    }

    private static Stream<Arguments> should_generate_token_from_valid_request() {

        var clientModelFixture = new ClientModelFixture();
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.authorization_code});
        var tokenRequestFixture = new TokenRequestModelFixture();

        var validTokenRequest = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        validTokenRequest.setClient_id(client.clientId());
        validTokenRequest.setClient_secret(client.clientSecret());

        var tokenRequestWithoutRefreshToken = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        tokenRequestWithoutRefreshToken.setClient_id(client.clientId());
        tokenRequestWithoutRefreshToken.setClient_secret(client.clientSecret());
        tokenRequestWithoutRefreshToken.setCode("");

        var tokenRequestWithoutClient = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        String authorizationHeader = Base64.getEncoder().withoutPadding().encodeToString((client.clientId() + ":" + client.clientSecret()).getBytes());
        var pair = Pair.of(client.clientId(), client.clientSecret());

        var tokenModel = TokenModel.builder().build();

        var authorizationCodeFixture = new AuthorizationCodeFixture();
        var authorizationCode = authorizationCodeFixture.createRandomOne();
        authorizationCode.setCode(validTokenRequest.code);
        authorizationCode.setRedirectUri(validTokenRequest.redirect_uri);

        return Stream.of(
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        authorizationCode,
                        new TokenResultModel(tokenModel, null),
                        pair),
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        null,
                        new TokenResultModel(null, "invalid_request"),
                        pair),
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        authorizationCode,
                        new TokenResultModel(null, "unauthorized_client"),
                        null)
        );
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(clientService);
        Mockito.reset(tokenService);
        Mockito.reset(authorizationCodeService);
    }

    @MethodSource
    @ParameterizedTest
    void should_validate_authorization_code_provider(ClientModel clientModel,
                                                String authorizationHeader,
                                                TokenRequestModel tokenRequest,
                                                ValidationResultModel expectedResult,
                                                Pair<String, String> clientCredentialsPair) {
        // Arrange
        if (!tokenRequest.client_id.isEmpty() && !tokenRequest.client_secret.isEmpty())
            when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                    .thenReturn(clientModel);
        if (!StringUtils.isEmpty(authorizationHeader))
            when(clientService.decodeClientCredentials(authorizationHeader))
                    .thenReturn(clientCredentialsPair == null ? Optional.empty() : Optional.of(clientCredentialsPair));

        // Act
        var result = tokenGrantProviderAuthorizationCode.validate(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request(ClientModel clientModel,
                                                  String authorizationHeader,
                                                  TokenRequestModel tokenRequest,
                                                  AuthorizationCode authorizationCode,
                                                  TokenResultModel tokenResultModel,
                                                  Pair<String, String> clientCredentialsPair) {

        // Arrange
        when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                .thenReturn(clientModel);
        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(clientCredentialsPair == null ? Optional.empty() : Optional.of(clientCredentialsPair));
        when(authorizationCodeService.getAuthorizationCode(tokenRequest.code, tokenRequest.redirect_uri, true))
                .thenReturn(authorizationCode);
        if(authorizationCode!=null){
            when(tokenService.generateToken(clientModel, authorizationCode.getUserId(), tokenRequest.state))
                    .thenReturn(tokenResultModel.getResult());
        }
        // Act
        var result = tokenGrantProviderAuthorizationCode.generateToken(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(tokenResultModel);
    }
}
