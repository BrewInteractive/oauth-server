package com.brew.oauth20.server.integration;


import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.fixture.ClientFixture;
import com.brew.oauth20.server.fixture.WebOriginModelFixture;
import com.brew.oauth20.server.model.UpdateClientLogoRequestModel;
import com.brew.oauth20.server.model.UpdateClientLogoResponseModel;
import com.brew.oauth20.server.model.WebOriginModel;
import com.brew.oauth20.server.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientControllerTest {
    private static Faker faker;
    private final Client client;
    private final List<WebOriginModel> webOriginModels;
    @MockBean
    private ClientService clientService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    public ClientControllerTest() {
        var clientFixture = new ClientFixture();
        var webOriginModelFixture = new WebOriginModelFixture();
        client = clientFixture.createRandomOne(false);
        webOriginModels = webOriginModelFixture.createRandomList(2);
    }

    private static Stream<Arguments> should_return_bad_request_when_request_has_param_validation_error() {
        var logoFile = ClientFixture.getClientLogo();
        return Stream.of(
                Arguments.of(
                        faker.lorem().word(),
                        UpdateClientLogoRequestModel.builder().build()
                )
        );
    }

    @BeforeAll
    void initialize() {
        faker = new Faker();
    }

    @MethodSource
    @ParameterizedTest
    void should_return_bad_request_when_request_has_param_validation_error(String clientId, UpdateClientLogoRequestModel request) throws Exception {
        // Arrange
        when(clientService.getWebOrigins(clientId)).thenReturn(webOriginModels);

        // Act
        var result = mockMvc.perform(post("/client/logo")
                        .header(HttpHeaders.ORIGIN, webOriginModels.stream()
                                .map(WebOriginModel::webOrigin)
                                .findFirst()
                                .get())
                        .param("client_id", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertEquals("invalid_request", response);
    }


    @Test
    void should_return_bad_request_when_request_has_body_validation_error() throws Exception {
        // Arrange
        when(clientService.getWebOrigins(client.getClientId())).thenReturn(webOriginModels);

        // Act
        var result = mockMvc.perform(post("/client/logo")
                        .header(HttpHeaders.ORIGIN, webOriginModels.stream()
                                .map(WebOriginModel::webOrigin)
                                .findFirst()
                                .get())
                        .param("client_id", client.getClientId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertEquals("invalid_request", response);
    }

    @Test
    void should_return_not_found_when_client_does_not_exist() throws Exception {
        // Arrange
        var logoFile = ClientFixture.getClientLogo();
        var requestModel = UpdateClientLogoRequestModel.builder().logoFile(logoFile).build();
        when(clientService.getWebOrigins(client.getClientId())).thenReturn(webOriginModels);

        // Act
        var result = mockMvc.perform(post("/client/logo")
                        .header(HttpHeaders.ORIGIN, webOriginModels.stream()
                                .map(WebOriginModel::webOrigin)
                                .findFirst()
                                .get())
                        .param("client_id", client.getClientId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel))
                )
                .andExpect(status().isNotFound())
                .andReturn();
        String response = result.getResponse().getContentAsString();

        // Assert
        assertEquals("client_not_found", response);
        verify(clientService, times(0)).setClientLogo(any(), any());
    }

    @Test
    void should_return_internal_server_error_when_io_exception_occurs() throws Exception {
        // Arrange
        var logoFile = ClientFixture.getClientLogo();
        var requestModel = UpdateClientLogoRequestModel.builder().logoFile(logoFile).build();
        when(clientService.existsByClientId(client.getClientId())).thenReturn(true);
        when(clientService.setClientLogo(client.getClientId(), logoFile)).thenThrow(new IOException());
        when(clientService.getWebOrigins(client.getClientId())).thenReturn(webOriginModels);

        // Act
        var result = mockMvc.perform(post("/client/logo")
                        .header(HttpHeaders.ORIGIN, webOriginModels.stream()
                                .map(WebOriginModel::webOrigin)
                                .findFirst()
                                .get())
                        .param("client_id", client.getClientId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel))
                )
                .andExpect(status().isInternalServerError())
                .andReturn();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertEquals("server_error", response);
    }

    @Test
    void should_return_ok_with_logo_url_when_request_is_valid_and_client_exists() throws Exception {
        // Arrange
        var logoFile = ClientFixture.getClientLogo();
        var logoUrl = faker.internet().url();
        var requestModel = UpdateClientLogoRequestModel.builder().logoFile(logoFile).build();
        var request = objectMapper.writeValueAsString(requestModel);
        var responseModel = UpdateClientLogoResponseModel.builder()
                .client_id(client.getClientId())
                .client_logo_url(logoUrl)
                .build();
        var expected = objectMapper.writeValueAsString(responseModel);

        when(clientService.existsByClientId(client.getClientId())).thenReturn(true);
        when(clientService.setClientLogo(client.getClientId(), logoFile)).thenReturn(logoUrl);
        when(clientService.getWebOrigins(client.getClientId())).thenReturn(webOriginModels);

        // Act
        var result = mockMvc.perform(post("/client/logo")
                        .header(HttpHeaders.ORIGIN, webOriginModels.stream()
                                .map(WebOriginModel::webOrigin)
                                .findFirst()
                                .get())
                        .param("client_id", client.getClientId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        // Assert
        assertEquals(expected, response);
    }
}
