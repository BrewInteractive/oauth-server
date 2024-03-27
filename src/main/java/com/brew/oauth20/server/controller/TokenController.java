package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.UnsupportedGrantTypeException;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.provider.tokengrant.BaseTokenGrantProvider;
import com.brew.oauth20.server.service.factory.TokenGrantProviderFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
public class TokenController {
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private final TokenGrantProviderFactory tokenGrantProviderFactory;

    @Autowired
    public TokenController(TokenGrantProviderFactory tokenGrantProviderFactory) {
        this.tokenGrantProviderFactory = tokenGrantProviderFactory;
    }

    @PostMapping(value = "/oauth/token")
    public ResponseEntity<Object> tokenPost(@Valid @RequestBody TokenRequestModel tokenRequestModel,
                                            BindingResult validationResult,
                                            HttpServletRequest request) {
        try {
            if (validationResult.hasErrors()) {
                logger.error("invalid_request");
                return new ResponseEntity<>(new TokenResultModel(null, "invalid_request"), HttpStatus.BAD_REQUEST);
            }

            var authorizationHeaderValue = request.getHeader(AUTHORIZATION_HEADER_KEY);

            var tokenGrantProvider = createTokenGrantProvider(tokenRequestModel);

            var tokenResponse = tokenGrantProvider.generateToken(authorizationHeaderValue, tokenRequestModel);

            if (tokenResponse.getError() != null) {
                return new ResponseEntity<>(tokenResponse.getError(), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(tokenResponse.getResult(), HttpStatus.OK);
            }
        } catch (UnsupportedGrantTypeException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new TokenResultModel(null, OAuthError.UNSUPPORTED_GRANT_TYPE.getValue()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>("system_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private BaseTokenGrantProvider createTokenGrantProvider(TokenRequestModel tokenRequestModel) {
        try {
            var tokenGrantProvider = tokenGrantProviderFactory
                    .getService(GrantType.fromValue(tokenRequestModel.grant_type));
            if (tokenGrantProvider == null)
                throw new UnsupportedGrantTypeException();
            return tokenGrantProvider;
        } catch (UnsupportedServiceTypeException e) {
            throw new UnsupportedGrantTypeException();
        }
    }
}
