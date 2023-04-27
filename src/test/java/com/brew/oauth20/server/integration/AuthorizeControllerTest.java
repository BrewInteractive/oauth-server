package com.brew.oauth20.server.integration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorizeControllerTest extends BaseAuthorizeControllerTest {


    //region /oauth/authorize tests

    @Test
    void should_not_redirect_with_no_parameter_invalid_request_post_test() throws Exception {
        // Act
        ResultActions resultActions = postAuthorize("", "", "", "");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_not_redirect_with_no_parameter_invalid_request_get_test() throws Exception {
        // Act
        ResultActions resultActions = getAuthorize("", "", "");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }


    @Test
    void should_not_redirect_with_invalid_uri_parameter_invalid_request_post_test() throws Exception {
        // Arrange
        String invalidRedirectUri = "redirect_uri";

        // Act
        ResultActions resultActions = postAuthorize(invalidRedirectUri, authorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_not_redirect_with_invalid_uri_parameter_invalid_request_get_test() throws Exception {
        // Arrange
        String invalidRedirectUri = "redirect_uri";

        // Act
        ResultActions resultActions = getAuthorize(invalidRedirectUri, authorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }


    @Test
    void should_redirect_unauthorized_client_post_test() throws Exception {
        // Arrange
        String unauthorizedClientId = "unauthorized_client_id";

        // Act
        ResultActions resultActions = postAuthorize(authorizedRedirectUri, unauthorizedClientId, "code");


        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(authorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }


    @Test
    void should_redirect_unauthorized_client_get_test() throws Exception {
        // Arrange
        String unauthorizedClientId = "unauthorized_client_id";

        // Act
        ResultActions resultActions = getAuthorize(authorizedRedirectUri, unauthorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(authorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }

    @Test
    void should_redirect_unauthorized_redirect_uri_unauthorized_client_post_test() throws Exception {
        // Act
        ResultActions resultActions = postAuthorize(notAuthorizedRedirectUri, authorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(notAuthorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }

    @Test
    void should_redirect_unauthorized_redirect_uri_unauthorized_client_get_test() throws Exception {
        // Act
        ResultActions resultActions = getAuthorize(notAuthorizedRedirectUri, authorizedClientId, "code");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(notAuthorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }

    @Test
    void should_redirect_unsupported_response_type_post_test() throws Exception {
        // Act
        ResultActions resultActions = postAuthorize(notAuthorizedRedirectUri, authorizedClientId, "unsupported_response_type");


        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(notAuthorizedRedirectUri)
                .contains("error=unsupported_response_type");
        assertThat(response.getContentAsString()).isEqualTo("unsupported_response_type");
    }

    @Test
    void should_redirect_unsupported_response_type_get_test() throws Exception {
        // Act
        ResultActions resultActions = getAuthorize(notAuthorizedRedirectUri, authorizedClientId, "unsupported_response_type");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(notAuthorizedRedirectUri)
                .contains("error=unsupported_response_type");
        assertThat(response.getContentAsString()).isEqualTo("unsupported_response_type");
    }

    @Test
    void should_redirect_to_login_post_test() throws Exception {
        // Act
        ResultActions resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, "");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());

        assertThat(locationHeader).contains(authorizedLoginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("state=%s".formatted(authorizedState))
                .doesNotContain("error");
    }


    @Test
    void should_redirect_to_login_get_test() throws Exception {
        // Act
        ResultActions resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code", authorizedState, "");

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(authorizedLoginSignupEndpoint)
                .contains("response_type=code")
                .contains("client_id=%s".formatted(authorizedClientId))
                .contains("redirect_uri=%s".formatted(authorizedRedirectUri))
                .contains("state=%s".formatted(authorizedState))
                .doesNotContain("error");
    }

    @Test
    void should_redirect_with_authorization_code_post_test() throws Exception {
        // Arrange
        long userId = faker.random().nextLong();

        // Act
        ResultActions resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", Long.toString(userId));


        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(authorizedRedirectUri)
                .contains("code=");

        var codeEntityList = authorizationCodeRepository.findAll();

        var codeEntity = codeEntityList.stream().filter(x -> x.getUserId() == userId).findAny().get();

        assertThat(codeEntity).isNotNull();
        assertThat(locationHeader).contains("code=" + codeEntity.getCode());
    }

    @Test
    void should_redirect_with_authorization_code_get_test() throws Exception {
        // Arrange
        long userId = faker.random().nextLong();

        // Act
        ResultActions resultActions = getAuthorize(authorizedRedirectUri, authorizedClientId, "code", Long.toString(userId));

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(authorizedRedirectUri)
                .contains("code=");

        var codeEntityList = authorizationCodeRepository.findAll();

        var codeEntity = codeEntityList.stream().filter(x -> x.getUserId() == userId).findAny().get();

        assertThat(codeEntity).isNotNull();
        assertThat(locationHeader).contains("code=" + codeEntity.getCode());
    }

    //endregion tests
}
