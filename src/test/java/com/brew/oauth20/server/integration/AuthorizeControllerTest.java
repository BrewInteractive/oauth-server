package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.mapper.RefreshTokenMapper;
import com.brew.oauth20.server.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

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
    private String authorizedLoginSignupEndpoint;
    private String authorizedState;

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
    @Autowired
    private Environment env;

    @BeforeAll
    void setup() {
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
        authorizedLoginSignupEndpoint = env.getProperty("LOGIN_SIGNUP_ENDPOINT", "https://test.com/login");
        authorizedState = faker.lordOfTheRings().character().replace(" ", "");

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

    private ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state, String cookieValue) throws Exception {
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("redirect_uri", redirectUri);
        requestBodyMap.put("client_id", clientId);
        requestBodyMap.put("response_type", responseType);
        requestBodyMap.put("state", state);

        String requestBody = new ObjectMapper().writeValueAsString(requestBodyMap);

        if (cookieValue.isBlank())
            return this.mockMvc.perform(post("/oauth/authorize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
        return this.mockMvc.perform(post("/oauth/authorize")
                .cookie(new Cookie("SESSION_ID", cookieValue))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

    }

    private ResultActions postAuthorize(String redirectUri, String clientId, String responseType) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", "");
    }

    private ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String cookieValue) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", cookieValue);
    }

    private ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state, String cookieValue) throws Exception {
        if (cookieValue.isBlank())
            return this.mockMvc.perform(get("/oauth/authorize")
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("client_id", clientId)
                    .queryParam("response_type", responseType)
                    .queryParam("state", state));
        return this.mockMvc.perform(get("/oauth/authorize")
                .cookie(new Cookie("SESSION_ID", cookieValue))
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", clientId)
                .queryParam("response_type", responseType)
                .queryParam("state", state));


    }

    private ResultActions getAuthorize(String redirectUri, String clientId, String responseType) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", "");
    }

    private ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String cookieValue) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", cookieValue);
    }

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
