package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.http.RestTemplateWrapper;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.mapper.RefreshTokenMapper;
import com.brew.oauth20.server.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ALL")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenControllerTest {

    private final String notAuthorizedRedirectUri = "http://www.not-authorized-uri.com";
    @Value("${id_token.user_identity_service_url}")
    String userIdentityServiceUrl;
    private String authorizedRedirectUri;
    private String authorizedAuthCode;
    private String authorizedClientId;
    private String authorizedClientSecret;
    private String authorizedRefreshToken;
    private String authorizedState;
    private String authorizedAuthorizationHeader;
    private ResponseEntity<JsonNode> userIdentityResponse;
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
    private ClientUserRepository clientUserRepository;
    @Autowired
    private ActiveRefreshTokenRepository activeRefreshTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @MockBean
    private RestTemplateWrapper restTemplateUserIdentityService;

    @BeforeAll
    void setup() {
        var faker = new Faker();
        var clientsGrantFixture = new ClientGrantFixture();
        var grantFixture = new GrantFixture();
        var redirectUrisFixture = new RedirectUriFixture();
        var authorizationCodeFixture = new AuthorizationCodeFixture();
        var activeAuthorizationCodeFixture = new ActiveAuthorizationCodeFixture();
        var clientsUserFixture = new ClientUserFixture();
        var activeRefreshTokenFixture = new ActiveRefreshTokenFixture();
        var userIdentityInfoFixture = new UserIdentityInfoFixture();

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

        var savedClientUser = clientUserRepository.save(clientsUser);

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
        authorizedState = faker.lordOfTheRings().location();
        userIdentityResponse = userIdentityInfoFixture.createRandomOneJsonResponse();
        authorizedAuthorizationHeader = "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", authorizedClientId, authorizedClientSecret).getBytes()).toString();
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

    @BeforeEach
    void reset() {
        Mockito.reset(restTemplateUserIdentityService);
    }

    @Test
    void should_return_token_grant_type_authorization_code_ok_test() throws Exception {
        when(restTemplateUserIdentityService.exchange(eq(userIdentityServiceUrl), eq(HttpMethod.GET), any(), eq(JsonNode.class))).thenReturn(userIdentityResponse);

        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.authorization_code.getGrantType() + "\"" +
                        ",\"code\":\"" + authorizedAuthCode + "\"" +
                        ",\"state\":\"" + authorizedState + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        var responseString = response.getContentAsString();

        assertThat(responseString).contains("Bearer")
                .contains("id_token")
                .contains("access_token")
                .contains("refresh_token")
                .contains("expires_in")
                .contains("token_type")
                .contains("state");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_token_with_authorization_header_test() throws Exception {
        when(restTemplateUserIdentityService.exchange(eq(userIdentityServiceUrl), eq(HttpMethod.GET), any(), eq(JsonNode.class))).thenReturn(userIdentityResponse);

        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.authorization_code.getGrantType() + "\"" +
                        ",\"code\":\"" + authorizedAuthCode + "\"" +
                        ",\"state\":\"" + authorizedState + "\"" +
                        "}")
                .header("Authorization", authorizedAuthorizationHeader));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        var responseString = response.getContentAsString();

        assertThat(responseString).contains("Bearer")
                .contains("id_token")
                .contains("access_token")
                .contains("refresh_token")
                .contains("expires_in")
                .contains("token_type")
                .contains("state");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_unauthorized_with_invalid_authorization_header_test() throws Exception {
        when(restTemplateUserIdentityService.exchange(eq(userIdentityServiceUrl), eq(HttpMethod.GET), any(), eq(JsonNode.class))).thenReturn(userIdentityResponse);

        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                                ",\"grant_type\":" + "\"" + GrantType.authorization_code.getGrantType() + "\"" +
                                ",\"code\":\"" + authorizedAuthCode + "\"" +
                                ",\"state\":\"" + authorizedState + "\"" +
                                "}")
                        .header("Authorization", "Basic dummyauth"))
                .andExpect(content().string("invalid_client"))
                .andExpect(status().isUnauthorized());
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
                        ",\"state\":\"" + authorizedState + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        var responseString = response.getContentAsString();

        assertThat(responseString).contains("Bearer")
                .contains("access_token")
                .contains("expires_in")
                .contains("token_type")
                .contains("state");
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
        var responseString = response.getContentAsString();

        assertThat(responseString).contains("Bearer")
                .contains("access_token")
                .contains("expires_in")
                .contains("token_type")
                .contains("state");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_token_grant_type_refresh_token_ok_test() throws Exception {
        when(restTemplateUserIdentityService.exchange(eq(userIdentityServiceUrl), eq(HttpMethod.GET), any(), eq(JsonNode.class))).thenReturn(userIdentityResponse);

        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                        ",\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.refresh_token.getGrantType() + "\"" +
                        ",\"refresh_token\":\"" + authorizedRefreshToken + "\"" +
                        ",\"state\":\"" + authorizedState + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        var responseString = response.getContentAsString();

        assertThat(responseString).contains("Bearer")
                .contains("id_token")
                .contains("access_token")
                .contains("refresh_token")
                .contains("expires_in")
                .contains("token_type")
                .contains("state");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_token_grant_type_refresh_token_ok_test_without_redirect_url() throws Exception {
        when(restTemplateUserIdentityService.exchange(eq(userIdentityServiceUrl), eq(HttpMethod.GET), any(), eq(JsonNode.class))).thenReturn(userIdentityResponse);

        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"client_id\":\"" + authorizedClientId + "\"" +
                        ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                        ",\"grant_type\":" + "\"" + GrantType.refresh_token.getGrantType() + "\"" +
                        ",\"refresh_token\":\"" + authorizedRefreshToken + "\"" +
                        ",\"state\":\"" + authorizedState + "\"" +
                        "}"));
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        var responseString = response.getContentAsString();

        assertThat(responseString).contains("Bearer")
                .contains("id_token")
                .contains("access_token")
                .contains("refresh_token")
                .contains("expires_in")
                .contains("token_type")
                .contains("state");
        resultActions.andExpect(status().isOk());
    }

    @Test
    void should_return_error_unsupported_grant_type_test() throws Exception {
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

        assertThat(response.getContentAsString()).contains("unsupported_grant_type");
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
    void should_return_invalid_client_error_when_client_is_invalid_test() throws Exception {
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

        assertThat(response.getContentAsString()).contains("invalid_client");
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_server_error_when_an_unexpected_exception_occurs_test() throws Exception {
        when(restTemplateUserIdentityService.exchange(eq(userIdentityServiceUrl), eq(HttpMethod.GET), any(), eq(JsonNode.class))).thenThrow(new RuntimeException("Intentional exception for testing"));

        this.mockMvc.perform(post("/oauth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"redirect_uri\":\"" + authorizedRedirectUri + "\"" +
                                ",\"client_id\":\"" + authorizedClientId + "\"" +
                                ",\"client_secret\":\"" + authorizedClientSecret + "\"" +
                                ",\"grant_type\":" + "\"" + GrantType.authorization_code.getGrantType() + "\"" +
                                ",\"code\":\"" + authorizedAuthCode + "\"" +
                                ",\"state\":\"" + authorizedState + "\"" +
                                "}"))
                .andExpect(content().string("server_error"))
                .andExpect(status().isInternalServerError());
    }
}
