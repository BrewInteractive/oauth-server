package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.testUtils.FakerUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class ClientValidatorTest {
    private static Faker faker;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    @Test
    void should_valid_client_return_valid_result() {

        // Arrange
        var responseType = FakerUtils.createRandomResponseType(faker);
        var redirectUriList = FakerUtils.createRandomRedirectUriList(faker);
        var expectedResult = new ValidationResultModel(true, null);
        //var clientModel = new ClientModel(FakerUtils.createRandomUUID(faker));

        // Act
        var clientValidator = new ClientValidator(responseType, redirectUriList);
        //var actualResult = clientValidator.validate(clientModel);

        // Assert
        //assertThat(actualResult).isEqualTo(expectedResult);
    }
}