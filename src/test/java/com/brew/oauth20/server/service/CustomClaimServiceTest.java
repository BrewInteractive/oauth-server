package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.enums.HookType;
import com.brew.oauth20.server.exception.CustomClaimHookException;
import com.brew.oauth20.server.fixture.CustomClaimFixture;
import com.brew.oauth20.server.fixture.HookModelFixture;
import com.brew.oauth20.server.http.RestTemplateWrapper;
import com.brew.oauth20.server.model.HookModel;
import com.brew.oauth20.server.service.impl.CustomClaimServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CustomClaimServiceTest {
    private static Faker faker;
    private HookModelFixture hookModelFixture;
    private CustomClaimFixture customClaimFixture;
    @Mock
    private RestTemplateWrapper restTemplate;
    @InjectMocks
    private CustomClaimServiceImpl customClaimService;

    @BeforeAll
    public static void init() {
        faker = new Faker();
    }

    @NotNull
    private static HttpEntity<LinkedMultiValueMap<Object, Object>> createValidRequest(HookModel hookModel, String userId) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        for (var hookHeader : hookModel.hookHeaderList())
            httpHeaders.add(hookHeader.key(), hookHeader.value());

        var requestBody = new LinkedMultiValueMap<>();
        requestBody.add("user_id", userId);
        return new HttpEntity<>(requestBody, httpHeaders);
    }

    @NotNull
    private static ResponseEntity<JsonNode> createValidResponse(Map<String, Object> expectedModel) {
        var objectMapper = new ObjectMapper();
        var responseJson = objectMapper.valueToTree(expectedModel);
        return new ResponseEntity<>(responseJson, HttpStatus.OK);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(restTemplate);
        hookModelFixture = new HookModelFixture();
        customClaimFixture = new CustomClaimFixture();
    }

    @Test
    void should_get_custom_claims() {
        // Arrange
        var hookModel = hookModelFixture.createRandomOne(HookType.custom_claim, 1);
        var userId = faker.letterify("?").repeat(20);
        var expectedModel = customClaimFixture.createRandomOne();

        HttpEntity<LinkedMultiValueMap<Object, Object>> requestEntity = createValidRequest(hookModel, userId);
        ResponseEntity<JsonNode> responseEntity = createValidResponse(expectedModel);

        when(restTemplate.exchange(hookModel.endpoint(), HttpMethod.POST, requestEntity, JsonNode.class))
                .thenReturn(responseEntity);

        // Act
        var actualResult = customClaimService.getCustomClaims(hookModel, userId);

        // Assert
        assertThat(actualResult)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedModel);
    }

    @Test
    void should_throw_exception_on_http_server_error() {
        // Arrange
        var hookModel = hookModelFixture.createRandomOne(HookType.custom_claim, 1);
        var userId = faker.letterify("?").repeat(20);

        HttpEntity<LinkedMultiValueMap<Object, Object>> requestEntity = createValidRequest(hookModel, userId);

        when(restTemplate.exchange(hookModel.endpoint(), HttpMethod.POST, requestEntity, JsonNode.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        // Act & Assert
        assertThatThrownBy(() -> customClaimService.getCustomClaims(hookModel, userId))
                .isInstanceOf(CustomClaimHookException.class);
    }
}
