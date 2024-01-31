package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.UserIdentityServiceException;
import com.brew.oauth20.server.fixture.UserIdentityInfoModelFixture;
import com.brew.oauth20.server.http.RestTemplateWrapper;
import com.brew.oauth20.server.model.UserIdentityInfoModel;
import com.brew.oauth20.server.service.impl.UserIdentityServiceImpl;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserIdentityServiceTest {
    private final Faker faker;
    @Value("${id_token.user_identity_service_url}")
    String userIdentityServiceUrl;
    @Mock
    private RestTemplateWrapper restTemplate;
    @InjectMocks
    private UserIdentityServiceImpl userIdentityService;

    UserIdentityServiceTest() {
        this.faker = new Faker();
    }

    @Test
    void should_get_user_identity_info_model() {
        // Arrange
        String accessToken = faker.letterify("?".repeat(64));

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        expectedHeaders.add("Authorization", accessToken);

        MultiValueMap<String, Object> expectedRequestBody = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, Object>> expectedRequestEntity = new HttpEntity<>(expectedRequestBody, expectedHeaders);

        UserIdentityInfoModel expectedModel = new UserIdentityInfoModelFixture().createRandomOne();

        ResponseEntity<UserIdentityInfoModel> responseEntity = new ResponseEntity<>(expectedModel, HttpStatus.OK);

        when(restTemplate.exchange(userIdentityServiceUrl, HttpMethod.GET, expectedRequestEntity, UserIdentityInfoModel.class))
                .thenReturn(responseEntity);

        // Act
        UserIdentityInfoModel result = userIdentityService.getUserIdentity(accessToken);

        // Assert
        assertThat(result).isNotNull()
                .isEqualTo(expectedModel);
    }

    @Test
    void should_throw_exception_on_http_server_error() {
        // Arrange
        String accessToken = faker.letterify("?".repeat(64));

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        expectedHeaders.add("Authorization", accessToken);

        MultiValueMap<String, Object> expectedRequestBody = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, Object>> expectedRequestEntity = new HttpEntity<>(expectedRequestBody, expectedHeaders);

        when(restTemplate.exchange(userIdentityServiceUrl, HttpMethod.GET, expectedRequestEntity, UserIdentityInfoModel.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        // Act & Assert
        assertThatThrownBy(() -> userIdentityService.getUserIdentity(accessToken))
                .isInstanceOf(UserIdentityServiceException.class);
    }
}
