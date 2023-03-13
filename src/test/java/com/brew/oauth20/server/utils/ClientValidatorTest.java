package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.RedirectUriModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientValidatorTest {
    private static Faker faker;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    @Test
    void should_valid_client_return_valid_result() {

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
}