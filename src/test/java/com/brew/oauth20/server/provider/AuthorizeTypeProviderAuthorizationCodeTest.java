package com.brew.oauth20.server.provider;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.ClientFixture;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderAuthorizationCode;
import com.brew.oauth20.server.service.ClientService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthorizeTypeProviderAuthorizationCodeTest {

    private static Faker faker;
    @MockBean
    private ClientService clientService;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    private static Stream<Arguments> shouldReturnValidResult() {

        var clientFixture = new ClientFixture();
        var client = clientFixture.createRandomOne(new ResponseType[]{ResponseType.code});
        var url = client.getRedirectUrises().stream().findFirst().get().getRedirectUri();
        return Stream.of(
                Arguments.of(Optional.of(client), client.getId(), url, new ValidationResultModel(true, null))
        );
    }


    @MethodSource
    @ParameterizedTest
    void shouldReturnValidResult(Optional<Client> client, UUID clientId, String url, ValidationResultModel validationResultModel) {
        Mockito.reset(clientService);
        when(clientService.getClient(clientId))
                .thenReturn(client);

        var provider = new AuthorizeTypeProviderAuthorizationCode(clientService);

        var result = provider.Validate(clientId, url);

        assertThat(result).isEqualTo(validationResultModel);
    }

    @Test
    void shouldReturnInvalidResult() {
        var clientId = UUID.randomUUID();
        var url = faker.internet().url();
        Mockito.reset(clientService);
        when(clientService.getClient(clientId))
                .thenReturn(Optional.ofNullable(null));

        var provider = new AuthorizeTypeProviderAuthorizationCode(clientService);

        var result = provider.Validate(clientId, url);

        assertThat(result.result()).isFalse();
        assertThat(result.error()).isEqualTo("unauthorized_client");
    }
}
