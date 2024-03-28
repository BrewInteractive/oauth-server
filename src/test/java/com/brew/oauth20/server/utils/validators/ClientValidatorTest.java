package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.RedirectUriModel;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.testUtils.FakerUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("java:S5778")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClientValidatorTest {
    private static Faker faker;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    private static Stream<Arguments> invalid_client_response_type_should_return_invalid_result() {
        return Stream.of(
                Arguments.of(
                        ResponseType.code,
                        1,
                        new ResponseType[]{ResponseType.token}
                ),
                Arguments.of(
                        ResponseType.token,
                        1,
                        new ResponseType[]{ResponseType.code}
                ),
                Arguments.of(
                        ResponseType.code,
                        2,
                        new ResponseType[]{ResponseType.token}
                )
        );
    }

    private static Stream<Arguments> invalid_client_grant_type_should_return_invalid_result() {
        return Stream.of(
                Arguments.of(
                        GrantType.authorization_code,
                        1,
                        new GrantType[]{GrantType.refresh_token, GrantType.client_credentials}
                ),
                Arguments.of(
                        GrantType.refresh_token,
                        1,
                        new GrantType[]{GrantType.authorization_code, GrantType.client_credentials}
                ),
                Arguments.of(
                        GrantType.authorization_code,
                        2,
                        new GrantType[]{GrantType.refresh_token, GrantType.client_credentials}
                )
        );
    }

    private static ResponseType getValidResponseType(ClientModel clientModel) {
        return clientModel.grantList().stream().map(GrantModel::responseType).findFirst().get();
    }

    private static String getValidRedirectUri(ClientModel clientModel) {
        return clientModel.redirectUriList().stream().map(RedirectUriModel::redirectUri).findFirst().get();
    }

    private static GrantType getValidGrantType(ClientModel clientModel) {
        return clientModel.grantList().stream().map(GrantModel::grantType).findFirst().get();
    }

    private static String getValidScope(ClientModel clientModel) {
        return clientModel.scopeList().stream()
                .map(scopeModel -> scopeModel.scope().getScope())
                .collect(Collectors.joining(" "));
    }


    @Test
    void validate_client_by_response_type_and_redirect_uri_should_return_valid_result() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validResponseType = getValidResponseType(clientModel);
        var validRedirectUri = getValidRedirectUri(clientModel);

        // Act
        var clientValidator = new ClientValidator(clientModel);
        var result = clientValidator.validate(validResponseType.getResponseType(), validRedirectUri, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_client_by_response_type_and_redirect_uri_and_scope_should_return_valid_result() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validResponseType = getValidResponseType(clientModel);
        var validRedirectUri = getValidRedirectUri(clientModel);
        var validScope = getValidScope(clientModel);

        // Act
        var clientValidator = new ClientValidator(clientModel);
        var result = clientValidator.validate(validResponseType.getResponseType(), validRedirectUri, validScope);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_client_by_grant_type_should_return_valid_result() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validGrantType = getValidGrantType(clientModel);

        // Act
        var clientValidator = new ClientValidator(clientModel);
        var result = clientValidator.validate(validGrantType.getGrantType());

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_client_without_scope_should_return_valid_result() {
        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validResponseType = getValidResponseType(clientModel);
        var validRedirectUri = getValidRedirectUri(clientModel);

        // Act
        var clientValidator = new ClientValidator(clientModel);
        var result = clientValidator.validate(validResponseType.getResponseType(), validRedirectUri, "");

        // Assert
        assertTrue(result);
    }

    @MethodSource
    @ParameterizedTest
    void invalid_client_response_type_should_return_invalid_result(ResponseType invalidResponseType, Integer grantSize, ResponseType[] responseTypeOptions) {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne(grantSize, responseTypeOptions);
        var validRedirectUri = getValidRedirectUri(clientModel);
        var validScope = getValidScope(clientModel);
        var expectedException = new OAuthException(OAuthError.UNSUPPORTED_RESPONSE_TYPE);

        // Act && Assert
        var clientValidator = new ClientValidator(clientModel);
        var actualException = assertThrows(OAuthException.class, () -> clientValidator.validate(invalidResponseType.name(), validRedirectUri, validScope));
        assertThat(actualException).usingRecursiveComparison().isEqualTo(expectedException);
    }

    @Test
    void invalid_client_redirect_uri_should_return_invalid_result() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validResponseType = getValidResponseType(clientModel);
        var invalidRedirectUri = FakerUtils.createRandomRedirectUri(faker);
        var validScope = getValidScope(clientModel);
        var expectedException = new OAuthException(OAuthError.INVALID_GRANT);

        // Act && Assert
        var clientValidator = new ClientValidator(clientModel);
        var actualException = assertThrows(OAuthException.class, () -> clientValidator.validate(validResponseType.getResponseType(), invalidRedirectUri, validScope));
        assertThat(actualException).usingRecursiveComparison().isEqualTo(expectedException);
    }

    @Test
    void invalid_client_scope_should_return_invalid_result() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validResponseType = getValidResponseType(clientModel);
        var validRedirectUri = getValidRedirectUri(clientModel);
        var invalidScope = faker.lordOfTheRings().location();
        var expectedException = new OAuthException(OAuthError.INVALID_SCOPE);

        // Act && Assert
        var clientValidator = new ClientValidator(clientModel);
        var actualException = assertThrows(OAuthException.class, () -> clientValidator.validate(validResponseType.getResponseType(), validRedirectUri, invalidScope));
        assertThat(actualException).usingRecursiveComparison().isEqualTo(expectedException);
    }

    @MethodSource
    @ParameterizedTest
    void invalid_client_grant_type_should_return_invalid_result(GrantType invalidGrantType, Integer grantSize, GrantType[] grantTypeOptions) {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne(grantSize, grantTypeOptions);
        var expectedException = new OAuthException(OAuthError.INVALID_SCOPE);

        // Act && Assert
        var clientValidator = new ClientValidator(clientModel);
        var actualException = assertThrows(OAuthException.class, () -> clientValidator.validate(invalidGrantType.name()));
        assertThat(actualException).usingRecursiveComparison().isEqualTo(expectedException);
    }
}