package com.brew.oauth20.server.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(properties = "oauth.login_signup_endpoint=")
class AuthorizeControllerNoLoginSignupEndpointTest extends BaseAuthorizeControllerTest {
    @Test
    void should_return_server_error_if_login_signup_endpoint_is_not_set() throws Exception {

        // Act
        ResultActions resultActions = postAuthorize(authorizedRedirectUri, authorizedClientId, "code", extraParameters);

        // Assert
        var response = resultActions.andReturn().getResponse();
        var locationHeader = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(locationHeader)
                .contains(errorPageUrl)
                .contains("error=server_error")
                .doesNotContain(extraParameterKey);
        assertThat(response.getContentAsString()).isEqualTo("server_error");
    }
}
