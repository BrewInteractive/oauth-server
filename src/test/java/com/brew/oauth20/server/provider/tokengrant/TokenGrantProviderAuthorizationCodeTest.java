package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.RefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.fixture.ActiveAuthorizationCodeFixture;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.fixture.RefreshTokenFixture;
import com.brew.oauth20.server.fixture.TokenRequestModelFixture;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import org.apache.commons.lang3.StringUtils;
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
class TokenGrantProviderAuthorizationCodeTest {
    @Mock
    AuthorizationCodeService authorizationCodeService;
    @Mock
    TokenService tokenService;
    @Mock
    ClientService clientService;
    @Mock
    RefreshTokenService refreshTokenService;

    @InjectMocks
    private TokenGrantProviderAuthorizationCode tokenGrantProviderAuthorizationCode;

    private static Stream<Arguments> should_validate_authorization_code_provider() {

        var clientModelFixture = new ClientModelFixture();
        var client = clientModelFixture.createRandomOne(1, new GrantType[]{GrantType.authorization_code});
        var tokenRequestFixture = new TokenRequestModelFixture();

        var validTokenRequest = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        validTokenRequest.setClient_id(client.clientId());
        validTokenRequest.setClient_secret(client.clientSecret());

        var tokenRequestWithoutCode = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        tokenRequestWithoutCode.setClient_id(client.clientId());
        tokenRequestWithoutCode.setClient_secret(client.clientSecret());
        tokenRequestWithoutCode.setCode("");

        var tokenRequestWithoutClient = tokenRequestFixture.createRandomOne(new GrantType[]{GrantType.authorization_code});
        tokenRequestWithoutClient.setClient_id("");
        tokenRequestWithoutClient.setClient_secret("");

        String authorizationHeader = Base64.getEncoder().withoutPadding().encodeToString((client.clientId() + ":" + client.clientSecret()).getBytes());

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

    private static Stream<Arguments> should_generate_token_from_valid_request_with_client_doesnt_issue_refresh_tokens() {

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
        var clientCredentialsPair = Pair.of(client.clientId(), client.clientSecret());

        var tokenModel = TokenModel.builder().build();

        var authorizationCodeFixture = new ActiveAuthorizationCodeFixture();
        var activeAuthorizationCode = authorizationCodeFixture.createRandomOne();
        activeAuthorizationCode.setCode(validTokenRequest.code);
        activeAuthorizationCode.setRedirectUri(validTokenRequest.redirect_uri);

        return Stream.of(
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        activeAuthorizationCode,
                        clientCredentialsPair,
                        new TokenResultModel(tokenModel, null)),
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        null,
                        clientCredentialsPair,
                        new TokenResultModel(null, "invalid_request"))

        );
    }

    private static Stream<Arguments> should_generate_token_from_valid_request_with_client_issues_refresh_tokens() {

        var clientModelFixture = new ClientModelFixture();
        var client = clientModelFixture.createRandomOne(1, true, new GrantType[]{GrantType.authorization_code});
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
        var clientCredentialsPair = Pair.of(client.clientId(), client.clientSecret());

        var tokenModel = TokenModel.builder().build();

        var authorizationCodeFixture = new ActiveAuthorizationCodeFixture();
        var activeAuthorizationCode = authorizationCodeFixture.createRandomOne();
        activeAuthorizationCode.setCode(validTokenRequest.code);
        activeAuthorizationCode.setRedirectUri(validTokenRequest.redirect_uri);

        var refreshTokenFixture = new RefreshTokenFixture();
        var refreshToken = refreshTokenFixture.createRandomOne();

        return Stream.of(
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        activeAuthorizationCode,
                        refreshToken,
                        clientCredentialsPair,
                        new TokenResultModel(tokenModel, null)),
                Arguments.of(client,
                        authorizationHeader,
                        validTokenRequest,
                        null,
                        refreshToken,
                        clientCredentialsPair,
                        new TokenResultModel(null, "invalid_request"))

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
                                                     Pair<String, String> clientCredentialsPair,
                                                     ValidationResultModel expectedResult
    ) {
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
    void should_generate_token_from_valid_request_with_client_doesnt_issue_refresh_tokens(ClientModel clientModel,
                                                                                          String authorizationHeader,
                                                                                          TokenRequestModel tokenRequest,
                                                                                          ActiveAuthorizationCode authorizationCode,
                                                                                          Pair<String, String> clientCredentialsPair,
                                                                                          TokenResultModel tokenResultModel
    ) {

        // Arrange
        when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                .thenReturn(clientModel);

        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(Optional.of(clientCredentialsPair));

        when(authorizationCodeService.getAuthorizationCode(tokenRequest.code, tokenRequest.redirect_uri, true))
                .thenReturn(authorizationCode);

        if (authorizationCode != null) {
            when(tokenService.generateToken(clientModel,
                    authorizationCode.getClientUser().getUserId(),
                    tokenRequest.state,
                    tokenRequest.additional_claims,
                    null))
                    .thenReturn(tokenResultModel.getResult());

        }
        // Act
        var result = tokenGrantProviderAuthorizationCode.generateToken(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(tokenResultModel);
    }

    @MethodSource
    @ParameterizedTest
    void should_generate_token_from_valid_request_with_client_issues_refresh_tokens(ClientModel clientModel,
                                                                                    String authorizationHeader,
                                                                                    TokenRequestModel tokenRequest,
                                                                                    ActiveAuthorizationCode authorizationCode,
                                                                                    RefreshToken refreshToken,
                                                                                    Pair<String, String> clientCredentialsPair,
                                                                                    TokenResultModel tokenResultModel
    ) {

        // Arrange
        when(clientService.getClient(tokenRequest.client_id, tokenRequest.client_secret))
                .thenReturn(clientModel);

        when(clientService.decodeClientCredentials(authorizationHeader))
                .thenReturn(Optional.of(clientCredentialsPair));

        when(authorizationCodeService.getAuthorizationCode(tokenRequest.code, tokenRequest.redirect_uri, true))
                .thenReturn(authorizationCode);

        if (authorizationCode != null) {
            when(refreshTokenService.createRefreshToken(tokenRequest.client_id, authorizationCode.getClientUser().getUserId(), clientModel.refreshTokenExpiresInDays()))
                    .thenReturn(refreshToken);

            when(tokenService.generateToken(clientModel,
                    authorizationCode.getClientUser().getUserId(),
                    tokenRequest.state,
                    tokenRequest.additional_claims,
                    refreshToken.getToken()))
                    .thenReturn(tokenResultModel.getResult());
        }
        // Act
        var result = tokenGrantProviderAuthorizationCode.generateToken(authorizationHeader, tokenRequest);

        // Assert
        assertThat(result).isEqualTo(tokenResultModel);
    }
}
