package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.repository.AuthorizationCodeRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(scripts = {"classpath:oauth-authorize/init-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthorizeControllerTest {

    private final String authorizedRedirectUri = "http://www.redirect_uri.com";
    private final String invalidRedirectUri = "redirect_uri";
    private final String notAuthorizedRedirectUri = "http://www.not-authorized-uri.com";


    private final String authorizedClientId = "i7GDRmtPPishVmCc5sHY42hppBUYIh3S";
    private final String unauthorizedClientId = "unauthorized_client_id";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    @Test
    void should_not_redirect_with_no_parameter_invalid_request_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isBadRequest());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_not_redirect_with_invalid_uri_parameter_invalid_request_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", invalidRedirectUri)
                .queryParam("client_id", unauthorizedClientId)
                .queryParam("response_type", "code"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isBadRequest());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_redirect_with_missing_parameters_invalid_request_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", authorizedRedirectUri));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("error=invalid_request");
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_redirect_unauthorized_client_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", authorizedRedirectUri)
                .queryParam("client_id", unauthorizedClientId)
                .queryParam("response_type", "code"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }

    @Test
    void should_redirect_unauthorized_redirect_uri_unauthorized_client_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", notAuthorizedRedirectUri)
                .queryParam("client_id", authorizedClientId)
                .queryParam("response_type", "code"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(notAuthorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }

    @Test
    void should_redirect_to_login() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", authorizedRedirectUri)
                .queryParam("client_id", authorizedClientId)
                .queryParam("response_type", "code"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isTemporaryRedirect());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("login");
    }

    @Test
    void should_redirect_with_authorization_code() throws Exception {
        Long userId = 12345L;
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .cookie(new Cookie("SESSION_ID", userId.toString()))
                .queryParam("redirect_uri", authorizedRedirectUri)
                .queryParam("client_id", authorizedClientId)
                .queryParam("response_type", "code"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("code=");

        var codeEntity = authorizationCodeRepository.findAll().get(0);

        assertThat(codeEntity).isNotNull();
        assertThat(location).contains("code=" + codeEntity.getCode());
    }

}
