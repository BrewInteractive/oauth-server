package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.RedirectUriModel;
import com.brew.oauth20.server.model.ValidationResultModel;
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

    @Test
    void validClientShouldReturnValidResult() {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne();

        var responseType = clientModel.grantList().stream().map(GrantModel::responseType).findFirst().get();
        var redirectUriList = clientModel.redirectUriList().stream().map(RedirectUriModel::redirectUri).toList();
        var expectedResult = new ValidationResultModel(true, null);

        // Act
        var clientValidator = new ClientValidator(responseType.name(), redirectUriList);
        var actualResult = clientValidator.validate(clientModel);

        // Assert
        assertEquals(expectedResult, actualResult);
    }

    @MethodSource
    @ParameterizedTest
    void invalidClientResponseTypeShouldReturnInvalidResult(ResponseType responseType, Integer grantSize, ResponseType[] responseTypeOptions) {

        // Arrange
        var clientModel = new ClientModelFixture().createRandomOne(grantSize, responseTypeOptions);
        var redirectUriList = clientModel.redirectUriList().stream().map(RedirectUriModel::redirectUri).toList();
        var expectedResult = new ValidationResultModel(false, "unauthorized_client");

        // Act
        var clientValidator = new ClientValidator(responseType.name(), redirectUriList);
        var actualResult = clientValidator.validate(clientModel);

        // Assert
        assertEquals(expectedResult, actualResult);
    }
}