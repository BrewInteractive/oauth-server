package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientAuthenticationFailedException;
import com.brew.oauth20.server.model.ClientCredentialsModel;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.service.UserIdentityService;
import com.brew.oauth20.server.utils.validators.ClientValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTokenGrantProvider {
    private static final String DEFAULT_ID_TOKEN_ENABLED = "false";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private final ClientService clientService;
    private final UserIdentityService userIdentityService;
    private final Environment env;
    protected TokenService tokenService;
    protected GrantType grantType;
    protected ClientModel client;


    @Autowired
    protected BaseTokenGrantProvider(ClientService clientService,
                                     TokenService tokenService,
                                     UserIdentityService userIdentityService,
                                     Environment env) {
        this.clientService = clientService;
        this.tokenService = tokenService;
        this.userIdentityService = userIdentityService;
        this.env = env;
    }

    public Boolean validate(ClientCredentialsModel clientCredentials, TokenRequestModel tokenRequest) {
        client = clientService.getClient(clientCredentials.getClientId(), clientCredentials.getClientSecret());
        if (client == null)
            throw new ClientAuthenticationFailedException();

        return new ClientValidator(client).validate(tokenRequest.getGrantType());
    }

    public abstract TokenModel generateToken(ClientCredentialsModel clientCredentials, TokenRequestModel tokenRequest);

    protected String generateIdToken(String accessToken, ClientModel client, String userId, String scope, Map<String, Object> additionalClaims) {
        if (!isIdTokenEnabled())
            return null;

        var userIdentityInfo = userIdentityService.getUserIdentityInfo(accessToken);
        if (additionalClaims == null)
            additionalClaims = new HashMap<>(Map.of());
        additionalClaims.putAll(userIdentityInfo);
        return this.tokenService.generateToken(client, userId, scope, additionalClaims);

    }

    private boolean isIdTokenEnabled() {
        return Boolean.parseBoolean(env.getProperty("id_token.enabled", DEFAULT_ID_TOKEN_ENABLED));
    }

    protected TokenModel buildToken(String accessToken, String state, long expiresIn) {
        return buildToken(accessToken, null, null, state, expiresIn);
    }

    protected TokenModel buildToken(String accessToken, String refreshToken, String idToken, String state, long expiresIn) {
        var tokenModelBuilder = TokenModel.builder()
                .accessToken(accessToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(expiresIn)
                .state(state);
        if (refreshToken != null)
            tokenModelBuilder.refreshToken(refreshToken);
        if (idToken != null)
            tokenModelBuilder.idToken(idToken);
        return tokenModelBuilder.build();
    }
}