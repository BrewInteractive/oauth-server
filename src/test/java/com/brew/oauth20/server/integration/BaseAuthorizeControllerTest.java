package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.ClientScope;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.RedirectUri;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.data.enums.Scope;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.repository.*;
import com.brew.oauth20.server.testUtils.FakerUtils;
import com.brew.oauth20.server.testUtils.ScopeUtils;
import com.brew.oauth20.server.utils.EncryptionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import jakarta.servlet.http.Cookie;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "SameParameterValue"})
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseAuthorizeControllerTest {

    protected Faker faker;
    protected ClientFixture clientFixture;
    protected GrantFixture grantFixture;
    protected ClientGrantFixture clientGrantFixture;
    protected RedirectUriFixture redirectUriFixture;
    protected ClientScopeFixture clientScopeFixture;
    protected ClientUserFixture clientUserFixture;
    protected ActiveAuthorizationCodeFixture activeAuthorizationCodeFixture;
    protected ActiveRefreshTokenFixture activeRefreshTokenFixture;
    protected ClientUserScopeFixture clientUserScopeFixture;
    @Value("${cookie.encryption.secret}")
    protected String cookieEncryptionSecret;
    @Value("${oauth.login_signup_endpoint}")
    protected String authorizedLoginSignupEndpoint;
    @Value("${cookie.encryption.algorithm}")
    protected String cookieEncryptionAlgorithm;
    protected String authorizedRedirectUri;
    protected String authorizedClientId;
    protected String authorizedUserId;
    protected String authorizedState;
    protected String authorizedScope;
    protected String notAuthorizedRedirectUri;
    @Autowired
    protected AuthorizationCodeRepository authorizationCodeRepository;
    @Autowired
    protected ActiveAuthorizationCodeRepository activeAuthorizationCodeRepository;
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected ClientGrantRepository clientGrantRepository;
    @Autowired
    protected GrantRepository grantRepository;
    @Autowired
    protected RedirectUriRepository redirectUriRepository;
    @Autowired
    protected ClientScopeRepository clientScopeRepository;
    @Autowired
    protected ClientUserRepository clientUserRepository;
    @Autowired
    protected ClientUserScopeRepository clientUserScopeRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.faker = new Faker();
        createFixtures();

        var savedClient = createClient();

        createClientGrants(savedClient);

        RedirectUri savedRedirectUri = createRedirectUri(savedClient);

        Set<ClientScope> savedClientScopes = createClientScopes(savedClient);

        ClientUser savedClientUser = createClientUser(savedClient);

        createClientUserScopes(savedClientScopes, savedClientUser);

        exposeTestVariables(savedClient, savedClientUser, savedRedirectUri, savedClientScopes);

    }

    private void exposeTestVariables(Client savedClient, ClientUser savedClientUser, RedirectUri savedRedirectUri, Set<ClientScope> savedClientScopes) {
        authorizedClientId = savedClient.getClientId();
        authorizedUserId = savedClientUser.getUserId();
        authorizedRedirectUri = savedRedirectUri.getRedirectUri();
        authorizedState = faker.lordOfTheRings().character().replace(" ", "");
        authorizedScope = ScopeUtils.createScopeString(savedClientScopes.stream().map(ClientScope::getScope).collect(Collectors.toSet()));

        notAuthorizedRedirectUri = FakerUtils.createRandomRedirectUri(faker);
    }

    private void createClientUserScopes(Set<ClientScope> clientScopes, ClientUser savedClientUser) {
        var clientUserScopes = clientScopes.stream().map(clientsScope -> clientUserScopeFixture.createRandomOne(savedClientUser, clientsScope.getScope())).collect(Collectors.toSet());
        clientUserScopeRepository.saveAll(clientUserScopes);
    }

    @NotNull
    private ClientUser createClientUser(Client savedClient) {
        var clientUser = clientUserFixture.createRandomOne(savedClient);
        return clientUserRepository.save(clientUser);
    }

    @NotNull
    private Set<ClientScope> createClientScopes(Client savedClient) {
        var clientScopes = clientScopeFixture.createRandomUniqueList(savedClient, Scope.values());
        clientScopeRepository.saveAll(clientScopes);
        return clientScopes;
    }

    @NotNull
    private RedirectUri createRedirectUri(Client savedClient) {
        var redirectUri = redirectUriFixture.createRandomOne(savedClient);
        return redirectUriRepository.save(redirectUri);
    }

    private void createClientGrants(Client savedClient) {
        var authorizationCodeGrant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code}, new GrantType[]{GrantType.authorization_code});
        var clientCredentialsGrant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code}, new GrantType[]{GrantType.client_credentials});
        var refreshTokenGrant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.code}, new GrantType[]{GrantType.refresh_token});

        var savedAuthorizationCodeGrant = grantRepository.save(authorizationCodeGrant);
        var savedclientCredentialsGrant = grantRepository.save(clientCredentialsGrant);
        var savedrefreshTokenGrant = grantRepository.save(refreshTokenGrant);

        var clientGrantAuthorizationCode = clientGrantFixture.createRandomOne(savedClient, savedAuthorizationCodeGrant);
        var clientGrantClientCredentials = clientGrantFixture.createRandomOne(savedClient, savedclientCredentialsGrant);
        var clientGrantRefreshToken = clientGrantFixture.createRandomOne(savedClient, savedrefreshTokenGrant);
        clientGrantRepository.saveAll(Arrays.asList(clientGrantAuthorizationCode, clientGrantClientCredentials, clientGrantRefreshToken));
    }

    @NotNull
    private Client createClient() {
        var client = clientFixture.createRandomOne(false);
        return clientRepository.save(client);
    }

    private void createFixtures() {
        this.clientFixture = new ClientFixture();
        this.grantFixture = new GrantFixture();
        this.clientGrantFixture = new ClientGrantFixture();
        this.redirectUriFixture = new RedirectUriFixture();
        this.clientScopeFixture = new ClientScopeFixture();
        this.clientUserFixture = new ClientUserFixture();
        this.activeAuthorizationCodeFixture = new ActiveAuthorizationCodeFixture();
        this.activeRefreshTokenFixture = new ActiveRefreshTokenFixture();
        this.clientUserScopeFixture = new ClientUserScopeFixture();
    }

    @AfterEach
    void emptyData() {
        authorizationCodeRepository.deleteAll();
        activeAuthorizationCodeRepository.deleteAll();
        clientGrantRepository.deleteAllInBatch();
        redirectUriRepository.deleteAllInBatch();
        clientUserScopeRepository.deleteAllInBatch();
        clientUserRepository.deleteAllInBatch();
        clientRepository.deleteAll();
        grantRepository.deleteAllInBatch();
    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state, String scope, Optional<String> userId) throws Exception {
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("redirect_uri", redirectUri);
        requestBodyMap.put("client_id", clientId);
        requestBodyMap.put("response_type", responseType);
        requestBodyMap.put("state", state);
        requestBodyMap.put("scope", scope);

        String requestBody = new ObjectMapper().writeValueAsString(requestBodyMap);

        if (userId.isEmpty())

            return this.mockMvc.perform(post("/oauth/authorize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

        var cookieValue = createCookieValue(userId.get());
        return this.mockMvc.perform(post("/oauth/authorize")
                .cookie(new Cookie("user", cookieValue))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", "", Optional.empty());
    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, state, "", Optional.empty());
    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state, String scope) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, state, scope, Optional.empty());
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state, String scope, Optional<String> userId) throws Exception {
        if (userId.isEmpty())
            return this.mockMvc.perform(get("/oauth/authorize")
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("client_id", clientId)
                    .queryParam("response_type", responseType)
                    .queryParam("state", state)
                    .queryParam("scope", scope)
            );
        var cookieValue = createCookieValue(userId.get());
        return this.mockMvc.perform(get("/oauth/authorize")
                .cookie(new Cookie("user", cookieValue))
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", clientId)
                .queryParam("response_type", responseType)
                .queryParam("state", state)
                .queryParam("scope", scope)
        );
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", "", Optional.empty());
    }

    protected ResultActions getAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", "", Optional.ofNullable(userId));
    }

    protected ResultActions postAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", "", Optional.ofNullable(userId));
    }

    protected ResultActions getAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId, String state, String scope) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, state, scope, Optional.ofNullable(userId));
    }

    protected ResultActions postAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId, String state, String scope) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, state, scope, Optional.ofNullable(userId));
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, state, "", Optional.empty());
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state, String scope) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, state, scope, Optional.empty());
    }

    private String createCookieValue(String userId) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        var expiresAt = OffsetDateTime.now().plusDays(2);
        var cookieValue = "{"
                + "\"user_id\": \"" + userId + "\","
                + "\"email\": \"" + faker.internet().emailAddress() + "\","
                + "\"country_code\": \"" + faker.phoneNumber().subscriberNumber() + "\","
                + "\"phone_number\": \"" + faker.phoneNumber().phoneNumber() + "\","
                + "\"expires_at\": " + expiresAt.toEpochSecond()
                + "}";
        return EncryptionUtils.encrypt(cookieValue, cookieEncryptionAlgorithm, cookieEncryptionSecret);
    }
}
