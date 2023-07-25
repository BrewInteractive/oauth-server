package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.WebOrigin;
import com.brew.oauth20.server.data.enums.FileStorageProvider;
import com.brew.oauth20.server.fixture.ClientFixture;
import com.brew.oauth20.server.fixture.WebOriginFixture;
import com.brew.oauth20.server.mapper.ClientMapper;
import com.brew.oauth20.server.mapper.WebOriginMapper;
import com.brew.oauth20.server.model.WebOriginModel;
import com.brew.oauth20.server.provider.filestorage.S3StorageProvider;
import com.brew.oauth20.server.repository.ClientRepository;
import com.brew.oauth20.server.repository.WebOriginRepository;
import com.brew.oauth20.server.service.factory.FileStorageProviderFactory;
import com.brew.oauth20.server.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClientServiceTest {
    @Value("${file.storage.provider}")
    String fileStorageProviderValue;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private WebOriginRepository webOriginRepository;
    @Mock
    private WebOriginMapper webOriginMapper;
    @Mock
    private FileStorageProviderFactory fileStorageProviderFactory;
    @Mock
    private S3StorageProvider s3StorageProvider;
    private ClientFixture clientFixture;
    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void should_get_client_by_client_id() {

        clientFixture = new ClientFixture();

        var client = clientFixture.createRandomOne(true);

        Optional<Client> optionalClient = Optional.of(client);

        when(clientRepository.findByClientId(client.getClientId()))
                .thenReturn(optionalClient);

        var expected = optionalClient.map(clientMapper::toDTO).orElse(null);

        var result = clientService.getClient(client.getClientId());

        assertThat(expected).isEqualTo(result);
    }

    @Test
    void should_get_client_by_client_id_client_secret() {

        clientFixture = new ClientFixture();

        var client = clientFixture.createRandomOne(true);

        Optional<Client> optionalClient = Optional.of(client);

        when(clientRepository.findByClientIdAndClientSecret(client.getClientId(), client.getClientSecret()))
                .thenReturn(optionalClient);

        var expected = optionalClient.map(clientMapper::toDTO).orElse(null);

        var result = clientService.getClient(client.getClientId(), client.getClientSecret());

        assertThat(expected).isEqualTo(result);
    }

    @Test
    void should_decode_client_credentials() {
        clientFixture = new ClientFixture();

        var client = clientFixture.createRandomOne(false);

        var decodeHeader = client.getClientId() + ":" + client.getClientSecret();
        String encodedHeader = Base64.getEncoder().withoutPadding().encodeToString(decodeHeader.getBytes());

        var pairResult = clientService.decodeClientCredentials(encodedHeader);

        assertThat(pairResult).isNotEmpty();
        assertThat(pairResult.get().getFirst()).isEqualTo(client.getClientId());
        assertThat(pairResult.get().getSecond()).isEqualTo(client.getClientSecret());
    }

    @Test
    void should_throw_exception_on_decode_client_credentials() {
        String encodedHeader = Base64.getEncoder().withoutPadding().encodeToString("malformed-header".getBytes());

        var pairResult = clientService.decodeClientCredentials(encodedHeader);

        assertThat(pairResult).isEmpty();
    }

    @Test
    void should_store_logo_and_return_url() throws IOException {
        // Arrange
        ReflectionTestUtils.setField(clientService, "fileStorageProviderValue", fileStorageProviderValue);

        String clientId = "testClient";

        String logoFile = ClientFixture.getClientLogo();

        String expectedUrl = String.format("logo-%s.jpg", clientId);

        byte[] logoBytes = Base64.getDecoder().decode(logoFile);

        when(fileStorageProviderFactory.getService(FileStorageProvider.s3)).thenReturn(s3StorageProvider);
        when(s3StorageProvider.store(logoBytes, expectedUrl)).thenReturn(expectedUrl);

        // Act
        String result = clientService.setClientLogo(clientId, logoFile);

        // Assert
        assertThat(result).isEqualTo(expectedUrl);
        verify(s3StorageProvider).store(logoBytes, "logo-" + clientId + ".jpg");
    }

    @Test
    void should_get_web_origins_by_client_id() {
        // Arrange
        var webOriginFixture = new WebOriginFixture();
        String clientId = "testClient";

        List<WebOrigin> webOrigins = webOriginFixture.createRandomList(5);
        when(webOriginRepository.findByClientId(clientId)).thenReturn(webOrigins);

        var expectedWebOriginModels = webOrigins.stream().map(WebOriginMapper.INSTANCE::toModel).toList();

        when(webOriginMapper.toModelList(webOrigins)).thenReturn(expectedWebOriginModels);

        // Act
        List<WebOriginModel> result = clientService.getWebOrigins(clientId);

        // Assert
        assertThat(result).isNotNull()
                .hasSize(expectedWebOriginModels.size())
                .containsExactlyElementsOf(expectedWebOriginModels);
    }
}

