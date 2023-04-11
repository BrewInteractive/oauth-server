package com.brew.oauth20.server.provider.authorizetype;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.ClientModelFixture;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.ClientService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AuthorizeTypeProviderAuthorizationCodeTest {

    private static Faker faker;
    @MockBean
    private ClientService clientService;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    private static Stream<Arguments> should_return_valid_result() {

        var clientModelFixture = new ClientModelFixture();
        var client = clientModelFixture.createRandomOne(1, new ResponseType[]{ResponseType.code});
        var url = client.redirectUriList().get(0).redirectUri();
        return Stream.of(
                Arguments.of(client, faker.letterify("????????????????"), url, new ValidationResultModel(true, null))
        );
    }


    @MethodSource
    @ParameterizedTest
    void should_return_valid_result(ClientModel clientModel, String clientId, String url, ValidationResultModel expectedValidationResult) {
        Mockito.reset(clientService);
        when(clientService.getClient(clientId))
                .thenReturn(clientModel);

        var provider = new AuthorizeTypeProviderAuthorizationCode(clientService);

        var actualValidationResult = provider.validate(clientId, url);

        assertThat(actualValidationResult).isEqualTo(expectedValidationResult);
    }

    @Test
    void should_return_invalid_result() {
        var clientId = UUID.randomUUID().toString();
        var url = faker.internet().url();
        Mockito.reset(clientService);
        when(clientService.getClient(clientId))
                .thenReturn(null);

        var provider = new AuthorizeTypeProviderAuthorizationCode(clientService);

        var validationResult = provider.validate(clientId, url);

        assertThat(validationResult.getResult()).isFalse();
        assertThat(validationResult.getError()).isEqualTo("unauthorized_client");
    }
}
