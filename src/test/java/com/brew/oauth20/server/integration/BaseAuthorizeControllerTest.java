package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.ClientScope;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.RedirectUri;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.HookType;
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
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
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
    protected HookFixture hookFixture;
    protected RedirectUriFixture redirectUriFixture;
    protected ClientScopeFixture clientScopeFixture;
    protected ClientUserFixture clientUserFixture;
    protected ActiveAuthorizationCodeFixture activeAuthorizationCodeFixture;
    protected ActiveRefreshTokenFixture activeRefreshTokenFixture;
    protected ClientUserScopeFixture clientUserScopeFixture;
    @Value("${cookie.encryption.secret}")
    protected String cookieEncryptionSecret;
    @Value("${oauth.login_signup_endpoint}")
    protected String loginSignupEndpoint;
    @Value("${oauth.consent_endpoint}")
    protected String consentEndpoint;
    @Value("${oauth.error_page_url}")
    protected String errorPageUrl;
    protected String authorizedRedirectUri;
    protected String authorizedClientId;
    protected String authorizedUserId;
    protected String authorizedState;
    protected String authorizedScope;
    protected String notAuthorizedRedirectUri;
    protected String extraParameterKey;
    protected String extraParameterValue;
    protected LinkedMultiValueMap<String, String> extraParameters;
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
    protected HookRepository hookRepository;
    @Autowired
    protected ClientUserRepository clientUserRepository;
    @Autowired
    protected ClientUserScopeRepository clientUserScopeRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.faker = new Faker();
        createExtraParameters();
        createFixtures();

        var savedClient = createClient();

        createClientGrants(savedClient);

        var savedRedirectUri = createRedirectUri(savedClient);

        createHooks(savedClient);

        var savedClientScopes = createClientScopes(savedClient);

        var savedClientUser = createClientUser(savedClient);

        createClientUserScopes(savedClientScopes, savedClientUser);

        exposeTestVariables(savedClient, savedClientUser, savedRedirectUri, savedClientScopes);

    }

    private void createExtraParameters() {
        extraParameterKey = faker.letterify("extra_parameter_???");
        extraParameterValue = faker.letterify("value_???");
        extraParameters = new LinkedMultiValueMap<>();
        extraParameters.add(extraParameterKey, extraParameterValue);
    }

    private void exposeTestVariables(Client savedClient, ClientUser savedClientUser, RedirectUri savedRedirectUri, Set<ClientScope> savedClientScopes) {
        authorizedClientId = savedClient.getClientId();
        authorizedUserId = savedClientUser.getUserId();
        authorizedRedirectUri = savedRedirectUri.getRedirectUri();
        authorizedState = faker.lordOfTheRings().character().replace(" ", "");
        authorizedScope = ScopeUtils.createScopeString(savedClientScopes.stream().map(ClientScope::getScope).collect(Collectors.toSet()));

        notAuthorizedRedirectUri = FakerUtils.createRandomUri(faker);
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

    private void createHooks(Client savedClient) {
        var hooks = hookFixture.createRandomUniqueList(savedClient, HookType.values());
        hookRepository.saveAll(hooks);
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
        var implicitGrant = grantFixture.createRandomOne(new ResponseType[]{ResponseType.token}, new GrantType[]{GrantType.implicit});

        var savedAuthorizationCodeGrant = grantRepository.save(authorizationCodeGrant);
        var savedClientCredentialsGrant = grantRepository.save(clientCredentialsGrant);
        var savedRefreshTokenGrant = grantRepository.save(refreshTokenGrant);
        var savedImplicitGrant = grantRepository.save(implicitGrant);

        var clientGrantAuthorizationCode = clientGrantFixture.createRandomOne(savedClient, savedAuthorizationCodeGrant);
        var clientGrantClientCredentials = clientGrantFixture.createRandomOne(savedClient, savedClientCredentialsGrant);
        var clientGrantRefreshToken = clientGrantFixture.createRandomOne(savedClient, savedRefreshTokenGrant);
        var clientGrantImplicit = clientGrantFixture.createRandomOne(savedClient, savedImplicitGrant);
        clientGrantRepository.saveAll(Arrays.asList(clientGrantAuthorizationCode, clientGrantClientCredentials, clientGrantRefreshToken, clientGrantImplicit));
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
        this.hookFixture = new HookFixture();
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

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state, String scope, Optional<String> userId, MultiValueMap<String, String> extraParameters) throws Exception {
        var requestBodyMap = new LinkedMultiValueMap<String, String>();
        requestBodyMap.add("redirect_uri", redirectUri);
        requestBodyMap.add("client_id", clientId);
        requestBodyMap.add("response_type", responseType);
        requestBodyMap.add("state", state);
        requestBodyMap.add("scope", scope);
        requestBodyMap.addAll(extraParameters);

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

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, MultiValueMap<String, String> extraParameters) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", "", Optional.empty(), extraParameters);
    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state, MultiValueMap<String, String> extraParameters) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, state, "", Optional.empty(), extraParameters);
    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state, String scope, MultiValueMap<String, String> extraParameters) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, state, scope, Optional.empty(), extraParameters);
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state, String scope, Optional<String> userId, MultiValueMap<String, String> extraParameters) throws Exception {
        var queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.add("redirect_uri", redirectUri);
        queryParams.add("client_id", clientId);
        queryParams.add("response_type", responseType);
        queryParams.add("state", state);
        queryParams.add("scope", scope);
        queryParams.addAll(extraParameters);

        if (userId.isPresent()) {
            var cookieValue = createCookieValue(userId.get());
            return this.mockMvc.perform(get("/oauth/authorize")
                    .cookie(new Cookie("user", cookieValue))
                    .queryParams(queryParams)
            );
        }
        return this.mockMvc.perform(get("/oauth/authorize")
                .queryParams(queryParams)
        );

    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, MultiValueMap<String, String> extraParameters) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", "", Optional.empty(), extraParameters);
    }

    protected ResultActions getAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId, MultiValueMap<String, String> extraParameters) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", "", Optional.ofNullable(userId), extraParameters);
    }

    protected ResultActions postAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId, MultiValueMap<String, String> extraParameters) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", "", Optional.ofNullable(userId), extraParameters);
    }

    protected ResultActions getAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId, String state, String scope, MultiValueMap<String, String> extraParameters) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, state, scope, Optional.ofNullable(userId), extraParameters);
    }

    protected ResultActions postAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId, String state, String scope, MultiValueMap<String, String> extraParameters) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, state, scope, Optional.ofNullable(userId), extraParameters);
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state, MultiValueMap<String, String> extraParameters) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, state, "", Optional.empty(), extraParameters);
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state, String scope, MultiValueMap<String, String> extraParameters) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, state, scope, Optional.empty(), extraParameters);
    }

    @SneakyThrows
    private String createCookieValue(String userId) {
        var expiresAt = OffsetDateTime.now().plusDays(2);
        var cookieValue = "{"
                + "\"user_id\": \"" + userId + "\","
                + "\"email\": \"" + faker.internet().emailAddress() + "\","
                + "\"country_code\": \"" + faker.phoneNumber().subscriberNumber() + "\","
                + "\"phone_number\": \"" + faker.phoneNumber().phoneNumber() + "\","
                + "\"expires_at\": " + expiresAt.toEpochSecond()
                + "}";
        return EncryptionUtils.encrypt(cookieValue, cookieEncryptionSecret);
    }
}
