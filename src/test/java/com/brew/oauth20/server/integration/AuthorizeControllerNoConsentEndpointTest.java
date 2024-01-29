package com.brew.oauth20.server.integration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(properties = "oauth.consent_endpoint=")
class AuthorizeControllerNoConsentEndpointTest extends BaseAuthorizeControllerTest {
    @Test
    void should_return_server_error_if_consent_endpoint_is_not_set() throws Exception {

        // Act
        ResultActions resultActions = postAuthorizeWithUserId(authorizedRedirectUri, authorizedClientId, "code", authorizedUserId, authorizedState, authorizedScope);

        // Assert
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader).contains(authorizedRedirectUri)
                .contains("error=server_error");
    }
}
