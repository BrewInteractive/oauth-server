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
    void should_not_redirect_with_no_parameter_invalid_request_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize("", "", "");
        else
            resultActions = postAuthorize("", "", "");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andExpect(status().isFound());
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
            resultActions = getAuthorize(invalidRedirectUri, authorizedClientId, "code");
        else
            resultActions = postAuthorize(invalidRedirectUri, authorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andExpect(status().isFound());
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
            resultActions = getAuthorize(authorizedRedirectUri, unauthorizedClientId, "code");
        else
            resultActions = postAuthorize(authorizedRedirectUri, unauthorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(authorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }


    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_unauthorized_redirect_uri_unauthorized_client_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(notAuthorizedRedirectUri, authorizedClientId, "code");
        else
            resultActions = postAuthorize(notAuthorizedRedirectUri, authorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(notAuthorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_unsupported_response_type_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(notAuthorizedRedirectUri, authorizedClientId, "unsupported_response_type");
        else
            resultActions = postAuthorize(notAuthorizedRedirectUri, authorizedClientId, "unsupported_response_type");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(notAuthorizedRedirectUri)
                .contains("error=unsupported_response_type");
        assertThat(response.getContentAsString()).isEqualTo("unsupported_response_type");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_to_login_with_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, authorizedScope);
        else
            resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, authorizedScope);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        assertThat(locationHeader).contains(loginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .contains("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")))
                .doesNotContain("error");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_to_login_with_state_and_without_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState);
        else
            resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        assertThat(locationHeader).contains(loginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .doesNotContain("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")))
                .doesNotContain("error");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_to_login_without_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code");
        else
            resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        assertThat(locationHeader).contains(loginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
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
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope);
        else
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope);


        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(consentEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")))
                .doesNotContain("error");
    }


    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_with_authorization_code_without_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId);
        else
            resultActions = postAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId);

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
                .contains("user_id=" + authorizedUserId)
                .doesNotContain("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .doesNotContain("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")));

    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void should_redirect_with_authorization_code_with_state_and_scope_test(String httpMethod) throws Exception {
        // Act
        ResultActions resultActions;
        if (httpMethod.equals(HttpMethod.GET.name()))
            resultActions = getAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope);
        else
            resultActions = postAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope);

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
                .contains("user_id=" + authorizedUserId)
                .contains("state=%s".formatted(URLEncoder.encode(authorizedState, StandardCharsets.UTF_8)))
                .contains("scope=%s".formatted(URLEncoder.encode(authorizedScope, StandardCharsets.UTF_8).replace("+", "%20")));
    }
}
