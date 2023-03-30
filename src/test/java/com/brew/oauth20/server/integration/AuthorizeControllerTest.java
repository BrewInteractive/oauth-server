package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.ClientFixture;
import com.brew.oauth20.server.fixture.ClientsGrantFixture;
import com.brew.oauth20.server.fixture.GrantFixture;
import com.brew.oauth20.server.fixture.RedirectUrisFixture;
import com.brew.oauth20.server.repository.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorizeControllerTest {

    private final String notAuthorizedRedirectUri = "http://www.not-authorized-uri.com";
    private String authorizedRedirectUri;
    private String authorizedClientId;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientsGrantRepository clientsGrantRepository;
    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private RedirectUrisRepository redirectUrisRepository;

    @BeforeAll
    void setup() {
        var clientFixture = new ClientFixture();
        var clientsGrantFixture = new ClientsGrantFixture();
        var grantFixture = new GrantFixture();
        var redirectUrisFixture = new RedirectUrisFixture();

        var client = clientFixture.createRandomOne(false);
        var grant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code});
        var clientsGrant = clientsGrantFixture.createRandomOne(new ResponseType[]{ResponseType.code});
        var redirectUris = redirectUrisFixture.createRandomOne();

        var savedClient = clientRepository.save(client);
        var savedGrant = grantRepository.save(grant);

        redirectUris.setClient(savedClient);
        redirectUrisRepository.save(redirectUris);

        clientsGrant.setClient(savedClient);
        clientsGrant.setGrant(savedGrant);
        clientsGrantRepository.save(clientsGrant);

        authorizedClientId = client.getClientId();
        authorizedRedirectUri = redirectUris.getRedirectUri();
    }

    @AfterAll
    void emptyData() {
        authorizationCodeRepository.deleteAll();
        clientsGrantRepository.deleteAllInBatch();
        redirectUrisRepository.deleteAllInBatch();
        clientRepository.deleteAll();
        grantRepository.deleteAllInBatch();
    }

    @Test
    void should_not_redirect_with_no_parameter_invalid_request_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_not_redirect_with_invalid_uri_parameter_invalid_request_test() throws Exception {
        String invalidRedirectUri = "redirect_uri";
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", invalidRedirectUri));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }


    @Test
    void should_redirect_unauthorized_client_test() throws Exception {
        String unauthorizedClientId = "unauthorized_client_id";
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
    void should_redirect_unsupported_response_type_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", notAuthorizedRedirectUri)
                .queryParam("client_id", authorizedClientId)
                .queryParam("response_type", "unsupported_response_type"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(notAuthorizedRedirectUri)
                .contains("error=unsupported_response_type");
        assertThat(response.getContentAsString()).isEqualTo("unsupported_response_type");
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
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("login");
    }

    @Test
    void should_redirect_with_authorization_code() throws Exception {
        long userId = 12345L;
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .cookie(new Cookie("SESSION_ID", userId + ""))
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
