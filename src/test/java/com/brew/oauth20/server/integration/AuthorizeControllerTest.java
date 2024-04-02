package com.brew.oauth20.server.integration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorizeControllerTest extends BaseAuthorizeControllerTest {
    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_to_login_with_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, authorizedScope, extraParameters);
        else
            resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, authorizedScope, extraParameters);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        assertThat(locationHeader)
                .contains(loginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .contains("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")))
                .contains("%s=%s".formatted(extraParameterKey, extraParameterValue))
                .doesNotContain("error");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_to_login_with_state_and_without_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, extraParameters);
        else
            resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, extraParameters);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        assertThat(locationHeader)
                .contains(loginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .contains("%s=%s".formatted(extraParameterKey, extraParameterValue))
                .doesNotContain("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")))
                .doesNotContain("error");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_to_login_without_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code", extraParameters);
        else
            resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", extraParameters);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        assertThat(locationHeader)
                .contains(loginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("%s=%s".formatted(extraParameterKey, extraParameterValue))
                .doesNotContain("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .doesNotContain("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")))
                .doesNotContain("error");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_to_consent_endpoint_when_scope_consent_required(String httpMethod) throws Exception {
        // Arrange
        // Remove one of the scopes from the client
        var allClientUserScopes = clientUserScopeRepository.findAll();
        clientUserScopeRepository.delete(allClientUserScopes.get(0));

        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope, extraParameters);
        else
            resultActions = postAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope, extraParameters);


        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(consentEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")))
                .contains("%s=%s".formatted(extraParameterKey, extraParameterValue))
                .doesNotContain("error");
    }


    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_with_authorization_code_without_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, extraParameters);
        else
            resultActions = postAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, extraParameters);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        var allClientUsers = clientUserRepository.findAll();
        var allAuthorizationCodes = authorizationCodeRepository.findAll();

        var expectedClientUserOptional = allClientUsers.stream()
                .filter(x -> x.getClient().getClientId().equals(authorizedClientId) &&
                        x.getUserId().equals(authorizedUserId))
                .findFirst();

        assertThat(expectedClientUserOptional).isPresent();

        var expectedAuthorizationCodeOptional = allAuthorizationCodes
                .stream()
                .filter(x -> x.getClientUser().equals(expectedClientUserOptional.get()))
                .findFirst();


        assertThat(expectedAuthorizationCodeOptional).isPresent();

        assertThat(locationHeader)
                .contains(authorizedRedirectUri)
                .contains("code=" + expectedAuthorizationCodeOptional.get().getCode())
                .contains("%s=%s".formatted(extraParameterKey, extraParameterValue))
                .doesNotContain("state=")
                .doesNotContain("user_id=")
                .doesNotContain("scope=")
                .doesNotContain("response_type=")
                .doesNotContain("redirect_uri=")
                .doesNotContain("client_id=");

    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_with_authorization_code_with_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope, extraParameters);
        else
            resultActions = postAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope, extraParameters);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        var allClientUsers = clientUserRepository.findAll();
        var allAuthorizationCodes = authorizationCodeRepository.findAll();

        var expectedClientUserOptional = allClientUsers.stream()
                .filter(x -> x.getClient().getClientId().equals(authorizedClientId) &&
                        x.getUserId().equals(authorizedUserId))
                .findFirst();

        assertThat(expectedClientUserOptional).isPresent();

        var expectedAuthorizationCodeOptional = allAuthorizationCodes.stream()
                .filter(x -> x.getClientUser().equals(expectedClientUserOptional.get()))
                .findFirst();


        assertThat(expectedAuthorizationCodeOptional).isPresent();

        assertThat(locationHeader)
                .contains("code=" + expectedAuthorizationCodeOptional.get().getCode())
                .contains("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .contains("%s=%s".formatted(extraParameterKey, extraParameterValue))
                .doesNotContain("user_id=")
                .doesNotContain("scope=")
                .doesNotContain("response_type=")
                .doesNotContain("redirect_uri=")
                .doesNotContain("client_id=");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_not_redirect_with_no_parameter_invalid_request_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize("", "", "", extraParameters);
        else
            resultActions = postAuthorize("", "", "", extraParameters);

        // Assert
        var response = resultActions.andReturn().getResponse();
        var locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(errorPageUrl)
                .contains("error=invalid_request")
                .doesNotContain(extraParameterKey);
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_not_redirect_with_invalid_uri_parameter_invalid_request_test(String httpMethod) throws Exception {
        // Arrange
        String invalidRedirectUri = "redirect_uri";

        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(invalidRedirectUri, authorizedClientId, "code", extraParameters);
        else
            resultActions = postAuthorize(invalidRedirectUri, authorizedClientId, "code", extraParameters);

        // Assert
        var response = resultActions.andReturn().getResponse();
        var locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(errorPageUrl)
                .contains("error=invalid_request")
                .doesNotContain(extraParameterKey);
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_unauthorized_client_test(String httpMethod) throws Exception {
        // Arrange
        String unauthorizedClientId = "unauthorized_client_id";

        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(authorizedRedirectUri, unauthorizedClientId, "code", extraParameters);
        else
            resultActions = postAuthorize(authorizedRedirectUri, unauthorizedClientId, "code", extraParameters);

        // Assert
        var response = resultActions.andReturn().getResponse();
        var locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(errorPageUrl)
                .contains("error=unauthorized_client")
                .doesNotContain(extraParameterKey);
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }


    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_invalid_grant_when_invalid_redirect_uri_used_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(notAuthorizedRedirectUri, authorizedClientId, "code", extraParameters);
        else
            resultActions = postAuthorize(notAuthorizedRedirectUri, authorizedClientId, "code", extraParameters);

        // Assert
        var response = resultActions.andReturn().getResponse();
        var locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(errorPageUrl)
                .contains("error=invalid_grant")
                .doesNotContain(extraParameterKey);
        assertThat(response.getContentAsString()).isEqualTo("invalid_grant");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_unsupported_response_type_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(notAuthorizedRedirectUri, authorizedClientId, "unsupported_response_type", extraParameters);
        else
            resultActions = postAuthorize(notAuthorizedRedirectUri, authorizedClientId, "unsupported_response_type", extraParameters);

        // Assert
        var response = resultActions.andReturn().getResponse();
        var locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(errorPageUrl)
                .contains("error=unsupported_response_type")
                .doesNotContain(extraParameterKey);
        assertThat(response.getContentAsString()).isEqualTo("unsupported_response_type");
    }


    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_return_unsupported_response_type_when_token_response_type_used(String httpMethod) throws Exception {
        // Arrange
        String tokenResponseType = "token"; // This is the unsupported response type

        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, tokenResponseType, authorizedUserId, extraParameters);
        else
            resultActions = postAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, tokenResponseType, authorizedUserId, extraParameters);

        // Assert
        var response = resultActions.andReturn().getResponse();
        var locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(errorPageUrl)
                .contains("error=unsupported_response_type")
                .doesNotContain(extraParameterKey);
        assertThat(response.getContentAsString()).isEqualTo("unsupported_response_type");
    }
}
