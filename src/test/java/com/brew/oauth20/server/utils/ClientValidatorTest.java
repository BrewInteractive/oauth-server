package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.RedirectUriModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientValidatorTest {
    private static Faker faker;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    private static Stream<Arguments> invalidClientResponseTypeShouldReturnInvalidResult() {
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

    private static ResponseType getValidResponseType(ClientModel clientModel) {
        return clientModel.grantList().stream().map(GrantModel::responseType).findFirst().get();

    }

    private static String getValidRedirectUri(ClientModel clientModel) {
        return clientModel.redirectUriList().stream().map(RedirectUriModel::redirectUri).findFirst().get();
    }

    @Test
    void validClientShouldReturnValidResult() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validResponseType = getValidResponseType(clientModel);
        var validRedirectUri = getValidRedirectUri(clientModel);
        var expectedResult = new ValidationResultModel(true, null);

        // Act
        var clientValidator = new ClientValidator(validResponseType.getResponseType(), validRedirectUri);
        var actualResult = clientValidator.validate(clientModel);

        // Assert
        assertEquals(expectedResult, actualResult);
    }

    @MethodSource
    @ParameterizedTest
    void invalidClientResponseTypeShouldReturnInvalidResult(ResponseType invalidResponseType, Integer grantSize, ResponseType[] responseTypeOptions) {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne(grantSize, responseTypeOptions);
        var validRedirectUri = getValidRedirectUri(clientModel);
        var expectedResult = new ValidationResultModel(false, "unauthorized_client");

        // Act
        var clientValidator = new ClientValidator(invalidResponseType.name(), validRedirectUri);
        var actualResult = clientValidator.validate(clientModel);

        // Assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidClientRedirectUriShouldReturnInvalidResult() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();
        var validResponseType = getValidResponseType(clientModel);
        var invalidRedirectUri = FakerUtils.createRandomRedirectUri(faker);
        var expectedResult = new ValidationResultModel(false, "unauthorized_client");

        // Act
        var clientValidator = new ClientValidator(validResponseType.getResponseType(), invalidRedirectUri);
        var actualResult = clientValidator.validate(clientModel);

        // Assert
        assertEquals(expectedResult, actualResult);
    }
}