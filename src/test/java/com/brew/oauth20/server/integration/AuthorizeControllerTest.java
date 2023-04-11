package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.mapper.RefreshTokenMapper;
import com.brew.oauth20.server.repository.*;
import com.github.javafaker.Faker;
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
    private String authorizedRefreshToken;

    private Faker faker;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Autowired
    private ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository;
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
    @Autowired
    private ActiveRefreshTokenRepository activeRefreshTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeAll
    void setup() {
        //TODO: should be refactored as single fixture and dbset
        this.faker = new Faker();
        var clientsGrantFixture = new ClientGrantFixture();
        var grantFixture = new GrantFixture();
        var redirectUrisFixture = new RedirectUriFixture();
        var authorizationCodeFixture = new AuthorizationCodeFixture();
        var activeAuthorizationCodeFixture = new ActiveAuthorizationCodeFixture();
        var clientsUserFixture = new ClientsUserFixture();
        var activeRefreshTokenFixture = new ActiveRefreshTokenFixture();

        var authCodeGrant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code}, new GrantType[]{GrantType.authorization_code});
        var clientCredGrant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code}, new GrantType[]{GrantType.client_credentials});
        var refreshTokenGrant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code}, new GrantType[]{GrantType.refresh_token});
        var clientsGrantAuthCode = clientsGrantFixture.createRandomOne(new ResponseType[]{ResponseType.code});
        var clientsGrantClientCred = clientsGrantFixture.createRandomOne(new ResponseType[]{ResponseType.code});
        var clientsGrantRefreshToken = clientsGrantFixture.createRandomOne(new ResponseType[]{ResponseType.code});
        var redirectUris = redirectUrisFixture.createRandomOne();
        var activeAuthorizationCode = activeAuthorizationCodeFixture.createRandomOne(redirectUris.getRedirectUri());

        var clientsUser = clientsUserFixture.createRandomOne();

        var client = clientsUser.getClient();

        var savedClient = clientRepository.save(client);

        var savedClientUser = clientsUserRepository.save(clientsUser);

        ActiveRefreshToken activeRefreshToken = activeRefreshTokenFixture.createRandomOne(savedClientUser);

        authorizedRefreshToken = activeRefreshToken.getToken();

        activeRefreshTokenRepository.save(activeRefreshToken);

        var existingRefreshToken = RefreshTokenMapper.INSTANCE.toRefreshToken(activeRefreshToken);

        refreshTokenRepository.save(existingRefreshToken);

        activeAuthorizationCode.setClient(savedClient);
        activeAuthorizationCode.setUserId(savedClientUser.getUserId());
        activeAuthorizationCodeRepository.save(activeAuthorizationCode);

        var authorizationCode = AuthorizationCodeMapper.INSTANCE.toAuthorizationCode(activeAuthorizationCode);

        authorizationCode.setClient(savedClient);
        authorizationCode.setUserId(savedClientUser.getUserId());
        authorizationCodeRepository.save(authorizationCode);

        var savedAuthCodeGrant = grantRepository.save(authCodeGrant);
        var savedClientCredGrant = grantRepository.save(clientCredGrant);
        var savedRefreshTokenGrant = grantRepository.save(refreshTokenGrant);

        redirectUris.setClient(savedClient);
        redirectUriRepository.save(redirectUris);

        clientsGrantAuthCode.setClient(savedClient);
        clientsGrantAuthCode.setGrant(savedAuthCodeGrant);
        clientGrantRepository.save(clientsGrantAuthCode);

        clientsGrantClientCred.setClient(savedClient);
        clientsGrantClientCred.setGrant(savedClientCredGrant);
        clientGrantRepository.save(clientsGrantClientCred);

        clientsGrantRefreshToken.setClient(savedClient);
        clientsGrantRefreshToken.setGrant(savedRefreshTokenGrant);
        clientGrantRepository.save(clientsGrantRefreshToken);

        authorizedClientId = client.getClientId();
        authorizedClientSecret = client.getClientSecret();
        authorizedRedirectUri = redirectUris.getRedirectUri();
        authorizedAuthCode = authorizationCode.getCode();
    }

    @AfterAll
    void emptyData() {
        authorizationCodeRepository.deleteAll();
        activeAuthorizationCodeRepository.deleteAll();
        clientGrantRepository.deleteAllInBatch();
        redirectUriRepository.deleteAllInBatch();
        clientRepository.deleteAll();
        grantRepository.deleteAllInBatch();
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
        long userId = faker.random().nextLong();
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
        long userId = faker.random().nextLong();
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

}
