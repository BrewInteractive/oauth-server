package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.mapper.RefreshTokenMapper;
import com.brew.oauth20.server.repository.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ALL")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenControllerTest {

    private final String notAuthorizedRedirectUri = "http://www.not-authorized-uri.com";
    private String authorizedRedirectUri;
    private String authorizedAuthCode;
    private String authorizedClientId;
    private String authorizedClientSecret;
    private String authorizedRefreshToken;
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

        activeAuthorizationCode.setClientUser(savedClientUser);
        activeAuthorizationCodeRepository.save(activeAuthorizationCode);

        var authorizationCode = AuthorizationCodeMapper.INSTANCE.toAuthorizationCode(activeAuthorizationCode);

        authorizationCode.setClientUser(savedClientUser);
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

    @AfterEach
    void delete() {
        //refreshTokenRepository.deleteAll();
    }

    //region /oauth/token tests
    @Test
    void should_return_token_grant_type_authorization_code_ok_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.authorization_code.getGrantType() + "\"" +
                        ",\"code\":\"" + authorizedAuthCode + "\"" +
                        ",\"additional_claims\":{\"claim1\":\"value1\"}" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("Bearer");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_error_invalid_request_when_grant_type_authorization_code_without_redirect_uri_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.authorization_code.getGrantType() + "\"" +
                        ",\"code\":\"" + authorizedAuthCode + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("invalid_request");
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void should_return_token_grant_type_client_credentials_ok_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.client_credentials.getGrantType() + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("Bearer");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_token_grant_type_client_credentials_ok_test_without_redirect_url() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.client_credentials.getGrantType() + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("Bearer");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_token_grant_type_refresh_token_ok_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.refresh_token.getGrantType() + "\"" +
                        ",\"refresh_token\":\"" + authorizedRefreshToken + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("Bearer");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_token_grant_type_refresh_token_ok_test_without_redirect_url() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.refresh_token.getGrantType() + "\"" +
                        ",\"refresh_token\":\"" + authorizedRefreshToken + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("Bearer");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_error_invalid_grant_type_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + "invalidgrant" + "\"" +
                        ",\"refresh_token\":\"" + authorizedRefreshToken + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("invalid_grant");
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void should_return_error_invalid_request_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"grant_type\":" + "\"" + GrantType.refresh_token.getGrantType() + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("invalid_request");
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void should_return_error_invalid_request_test_authorization_code_without_redirect_uri() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"grant_type\":" + "\"" + GrantType.authorization_code.getGrantType() + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("invalid_request");
        resultActions.andExpect(status().isBadRequest());
    }


    @Test
    void should_return_error_grant_type_refresh_token_unauthorized_client_test() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + "invalid_client" + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.refresh_token.getGrantType() + "\"" +
                        ",\"refresh_token\":\"" + authorizedRefreshToken + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getContentAsString()).contains("unauthorized_client");
        resultActions.andExpect(status().isBadRequest());
    }
    //endregion

}
