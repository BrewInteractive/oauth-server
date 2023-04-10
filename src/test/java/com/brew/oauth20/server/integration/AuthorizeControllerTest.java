package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.repository.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ALL")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorizeControllerTest {

    private final String notAuthorizedRedirectUri = "http://www.not-authorized-uri.com";
    private String authorizedRedirectUri;
    private String authorizedAuthCode;
    private String authorizedClientId;
    private String authorizedClientSecret;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientGrantRepository clientGrantRepository;
    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private RedirectUriRepository redirectUriRepository;
    @Autowired
    private ClientsUserRepository clientsUserRepository;

    @BeforeAll
    void setup() {
        var clientsGrantFixture = new ClientGrantFixture();
        var grantFixture = new GrantFixture();
        var redirectUrisFixture = new RedirectUriFixture();
        var authorizationCodeFixture = new AuthorizationCodeFixture();
        var clientsUserFixture = new ClientsUserFixture();

        var grant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code}, new GrantType[]{GrantType.authorization_code});
        var clientsGrant = clientsGrantFixture.createRandomOne(new ResponseType[]{ResponseType.code});
        var redirectUris = redirectUrisFixture.createRandomOne();
        var authorizationCode = authorizationCodeFixture.createRandomOne(redirectUris.getRedirectUri());

        var clientsUser = clientsUserFixture.createRandomOne();

        var client = clientsUser.getClient();


        var savedClient = clientRepository.save(client);

        var savedClientUser = clientsUserRepository.save(clientsUser);

        authorizationCode.setClient(savedClient);
        authorizationCode.setUserId(savedClientUser.getUserId());
        authorizationCodeRepository.save(authorizationCode);

        var savedGrant = grantRepository.save(grant);

        redirectUris.setClient(savedClient);
        redirectUriRepository.save(redirectUris);

        clientsGrant.setClient(savedClient);
        clientsGrant.setGrant(savedGrant);
        clientGrantRepository.save(clientsGrant);

        authorizedClientId = client.getClientId();
        authorizedClientSecret = client.getClientSecret();
        authorizedRedirectUri = redirectUris.getRedirectUri();
        authorizedAuthCode = authorizationCode.getCode();
    }

    @AfterAll
    void emptyData() {
        authorizationCodeRepository.deleteAll();
        clientGrantRepository.deleteAllInBatch();
        redirectUriRepository.deleteAllInBatch();
        clientRepository.deleteAll();
        grantRepository.deleteAllInBatch();
    }

    @AfterEach
    void delete() {
        //refreshTokenRepository.deleteAll();
    }

    //region /oauth/authorize tests

    @Test
    void should_not_redirect_with_no_parameter_invalid_request_post_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_not_redirect_with_no_parameter_invalid_request_get_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_not_redirect_with_invalid_uri_parameter_invalid_request_post_test() throws Exception {
        String invalidRedirectUri = "redirect_uri";
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + invalidRedirectUri + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }

    @Test
    void should_not_redirect_with_invalid_uri_parameter_invalid_request_get_test() throws Exception {
        String invalidRedirectUri = "redirect_uri";
        ResultActions resultActions = this.mockMvc.perform(get("/oauth/authorize")
                .queryParam("redirect_uri", invalidRedirectUri));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        resultActions.andExpect(status().isFound());
        assertThat(response.getContentAsString()).isEqualTo("invalid_request");
    }


    @Test
    void should_redirect_unauthorized_client_post_test() throws Exception {
        String unauthorizedClientId = "unauthorized_client_id";
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + unauthorizedClientId + "\"" +
                        ",\"response_type\":" + "\"code\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }


    @Test
    void should_redirect_unauthorized_client_get_test() throws Exception {
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
    void should_redirect_unauthorized_redirect_uri_unauthorized_client_post_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + notAuthorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"response_type\":" + "\"code\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(notAuthorizedRedirectUri)
                .contains("error=unauthorized_client");
        assertThat(response.getContentAsString()).isEqualTo("unauthorized_client");
    }

    @Test
    void should_redirect_unauthorized_redirect_uri_unauthorized_client_get_test() throws Exception {
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
    void should_redirect_unsupported_response_type_post_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + notAuthorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"response_type\":" + "\"unsupported_response_type\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(notAuthorizedRedirectUri)
                .contains("error=unsupported_response_type");
        assertThat(response.getContentAsString()).isEqualTo("unsupported_response_type");
    }

    @Test
    void should_redirect_unsupported_response_type_get_test() throws Exception {
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
    void should_redirect_to_login_post_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"response_type\":" + "\"code\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("login");
    }

    @Test
    void should_redirect_to_login_get_test() throws Exception {
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
    void should_redirect_with_authorization_code_post_test() throws Exception {
        long userId = 1234L;
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/authorize")
                .cookie(new Cookie("SESSION_ID", userId + ""))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"response_type\":" + "\"code\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String location = response.getHeader(LOCATION);
        resultActions.andExpect(status().isFound());
        assertThat(location).contains(authorizedRedirectUri)
                .contains("code=");

        var codeEntityList = authorizationCodeRepository.findAll();

        var codeEntity = codeEntityList.stream().filter(x -> x.getUserId() == userId).findAny().get();

        assertThat(codeEntity).isNotNull();
        assertThat(location).contains("code=" + codeEntity.getCode());
    }

    @Test
    void should_redirect_with_authorization_code_get_test() throws Exception {
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

        var codeEntityList = authorizationCodeRepository.findAll();

        var codeEntity = codeEntityList.stream().filter(x -> x.getUserId() == userId).findAny().get();

        assertThat(codeEntity).isNotNull();
        assertThat(location).contains("code=" + codeEntity.getCode());
    }

    //endregion tests

    //region /oauth/token tests
    @Test
    void should_return_token_with_200_post_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"authorization_code\"" +
                        ",\"code\":\"" + authorizedAuthCode + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("Bearer");
        resultActions.andExpect(status().isOk());
    }
    //endregion

}
