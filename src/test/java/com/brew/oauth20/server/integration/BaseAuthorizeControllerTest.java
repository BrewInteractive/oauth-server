package com.brew.oauth20.server.integration;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.*;
import com.brew.oauth20.server.mapper.AuthorizationCodeMapper;
import com.brew.oauth20.server.mapper.RefreshTokenMapper;
import com.brew.oauth20.server.repository.*;
import com.brew.oauth20.server.utils.EncryptionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import jakarta.servlet.http.Cookie;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "SameParameterValue"})
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseAuthorizeControllerTest {
    protected final String notAuthorizedRedirectUri = "http://www.not-authorized-uri.com";
    @Value("${cookie.encryption.secret}")
    protected String cookieEncryptionSecret;
    @Value("${oauth.login_signup_endpoint}")
    protected String authorizedLoginSignupEndpoint;
    @Value("${cookie.encryption.algorithm}")
    protected String cookieEncryptionAlgorithm;
    protected String authorizedRedirectUri;
    protected String authorizedAuthCode;
    protected String authorizedClientId;
    protected String authorizedClientSecret;
    protected String authorizedRefreshToken;
    protected String authorizedState;
    protected Faker faker;
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
    protected ClientsUserRepository clientsUserRepository;
    @Autowired
    protected ActiveRefreshTokenRepository activeRefreshTokenRepository;
    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setup() {
        this.faker = new Faker();
        var clientsGrantFixture = new ClientGrantFixture();
        var grantFixture = new GrantFixture();
        var redirectUrisFixture = new RedirectUriFixture();
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

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state, Optional<String> userId) throws Exception {
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("redirect_uri", redirectUri);
        requestBodyMap.put("client_id", clientId);
        requestBodyMap.put("response_type", responseType);
        requestBodyMap.put("state", state);

        String requestBody = new ObjectMapper().writeValueAsString(requestBodyMap);

        if (userId.isEmpty())

            return this.mockMvc.perform(post("/oauth/authorize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

        var cookieValue = createCookieValue(String.valueOf(userId.get()));
        return this.mockMvc.perform(post("/oauth/authorize")
                .cookie(new Cookie("user", cookieValue))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", Optional.empty());
    }


    protected ResultActions postAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, "", Optional.ofNullable(userId));
    }

    protected ResultActions postAuthorize(String redirectUri, String clientId, String responseType, String state) throws Exception {
        return postAuthorize(redirectUri, clientId, responseType, state, Optional.empty());
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state, Optional<String> userId) throws Exception {
        if (userId.isEmpty())
            return this.mockMvc.perform(get("/oauth/authorize")
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("client_id", clientId)
                    .queryParam("response_type", responseType)
                    .queryParam("state", state));
        var cookieValue = createCookieValue(String.valueOf(userId.get()));
        return this.mockMvc.perform(get("/oauth/authorize")
                .cookie(new Cookie("user", cookieValue))
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", clientId)
                .queryParam("response_type", responseType)
                .queryParam("state", state));
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", Optional.empty());
    }

    protected ResultActions getAuthorizeWithUserId(String redirectUri, String clientId, String responseType, String userId) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, "", Optional.ofNullable(userId));
    }

    protected ResultActions getAuthorize(String redirectUri, String clientId, String responseType, String state) throws Exception {
        return getAuthorize(redirectUri, clientId, responseType, state, Optional.empty());
    }

    private String createCookieValue(String userId) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        var expiresAt = OffsetDateTime.now().plusDays(2);
        var cookieValue = String.format("user_id=%s;email=%s;country_code=%s;phone_number=%s;expires_at=%d",
                userId, faker.internet().emailAddress(), faker.phoneNumber().subscriberNumber(), faker.phoneNumber().phoneNumber(), expiresAt.toEpochSecond());

        return EncryptionUtils.encrypt(cookieValue, cookieEncryptionAlgorithm, cookieEncryptionSecret);
    }
}
