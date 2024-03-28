package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.controller.base.BaseController;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.ClientAuthenticationFailedException;
import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.ClientCredentialsModel;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.provider.tokengrant.BaseTokenGrantProvider;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.factory.TokenGrantProviderFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private final TokenGrantProviderFactory tokenGrantProviderFactory;
    private final ClientService clientService;

    @Autowired
    public TokenController(ClientService clientService,
                           TokenGrantProviderFactory tokenGrantProviderFactory) {
        this.clientService = clientService;
        this.tokenGrantProviderFactory = tokenGrantProviderFactory;
    }

    @PostMapping(value = "/oauth/token")
    public ResponseEntity<Object> tokenPost(@Valid @RequestBody TokenRequestModel tokenRequestModel,
                                            BindingResult validationResult,
                                            HttpServletRequest request) {
        try {
            validateRequest(validationResult);
            var clientCredentials = getClientCredentials(request.getHeader(AUTHORIZATION_HEADER_KEY), tokenRequestModel);

            var tokenGrantProvider = createTokenGrantProvider(tokenRequestModel);

            var token = tokenGrantProvider.generateToken(clientCredentials, tokenRequestModel);
            return new ResponseEntity<>(token, HttpStatus.OK);

        } catch (ClientAuthenticationFailedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(OAuthError.INVALID_CLIENT.getValue(), HttpStatus.UNAUTHORIZED);
        } catch (OAuthException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(OAuthError.SERVER_ERROR.getValue(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ClientCredentialsModel getClientCredentials(String authorizationHeader, TokenRequestModel tokenRequest) {
        if (!StringUtils.isEmpty(authorizationHeader)) {
            var clientCredentials = clientService.decodeClientCredentials(authorizationHeader);
            if (clientCredentials.isEmpty())
                throw new ClientAuthenticationFailedException();
            return clientCredentials.get();
        }
        return new ClientCredentialsModel(tokenRequest.getClientId(), tokenRequest.getClientSecret());
    }

    private BaseTokenGrantProvider createTokenGrantProvider(TokenRequestModel tokenRequestModel) {
        try {
            var tokenGrantProvider = tokenGrantProviderFactory
                    .getService(GrantType.fromValue(tokenRequestModel.getGrantType()));
            if (tokenGrantProvider == null)
                throw new OAuthException(OAuthError.UNSUPPORTED_GRANT_TYPE);
            return tokenGrantProvider;
        } catch (UnsupportedServiceTypeException e) {
            throw new OAuthException(OAuthError.UNSUPPORTED_GRANT_TYPE);
        }
    }
}
