package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.HookType;
import com.brew.oauth20.server.model.*;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.CustomClaimService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.service.UserIdentityService;
import com.brew.oauth20.server.utils.validators.ClientValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTokenGrantProvider {
    private static final String DEFAULT_ID_TOKEN_ENABLED = "false";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private final ClientService clientService;
    private final CustomClaimService customClaimService;
    private final UserIdentityService userIdentityService;
    private final Environment env;
    protected TokenService tokenService;
    protected GrantType grantType;
    protected ClientModel client;


    @Autowired
    protected BaseTokenGrantProvider(ClientService clientService,
                                     TokenService tokenService,
                                     CustomClaimService customClaimService,
                                     UserIdentityService userIdentityService,
                                     Environment env) {
        this.clientService = clientService;
        this.tokenService = tokenService;
        this.customClaimService = customClaimService;
        this.userIdentityService = userIdentityService;
        this.env = env;
    }

    public ValidationResultModel validate(String authorizationHeader, TokenRequestModel tokenRequest) {
        String clientId;
        String clientSecret;
        if (StringUtils.isEmpty(authorizationHeader)) {
            clientId = tokenRequest.client_id;
            clientSecret = tokenRequest.client_secret;
        } else {
            var clientCredentials = clientService.decodeClientCredentials(authorizationHeader);
            if (clientCredentials.isEmpty())
                return new ValidationResultModel(false, "unauthorized_client");
            clientId = clientCredentials.get().getFirst();
            clientSecret = clientCredentials.get().getSecond();
        }

        client = clientService.getClient(clientId, clientSecret);

        if (client == null)
            return new ValidationResultModel(false, "unauthorized_client");

        ClientValidator clientValidator = new ClientValidator(client);

        return clientValidator.validate(tokenRequest.grant_type);
    }

    public abstract TokenResultModel generateToken(String authorizationHeader, TokenRequestModel tokenRequest);

    protected Map<String, Object> getCustomClaims(ClientModel client, String userId) {
        var customClaimHook = client.hookList().stream().filter(x -> x.hookType().equals(HookType.custom_claim)).findFirst();
        if (customClaimHook.isEmpty())
            return Map.of();
        return customClaimService.getCustomClaims(customClaimHook.get(), userId);
    }

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