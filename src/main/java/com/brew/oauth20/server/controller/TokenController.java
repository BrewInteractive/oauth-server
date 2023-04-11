package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.TokenRequestModel;
import com.brew.oauth20.server.model.TokenResultModel;
import com.brew.oauth20.server.provider.tokengrant.TokenGrantProviderFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    private final String authorizationHeaderKey;
    private final TokenGrantProviderFactory tokenGrantProviderFactory;

    public TokenController(TokenGrantProviderFactory tokenGrantProviderFactory) {
        this.tokenGrantProviderFactory = tokenGrantProviderFactory;
        this.authorizationHeaderKey = "Authorization";
    }

    @PostMapping(value = "/oauth/token")
    public ResponseEntity<Object> tokenPost(@Valid @RequestBody TokenRequestModel tokenRequestModel,
                                                      BindingResult validationResult,
                                                      HttpServletRequest request) {
        try{
            if (validationResult.hasErrors()) {
                return new ResponseEntity<>(new TokenResultModel(null, "invalid_request"), HttpStatus.BAD_REQUEST);
            }

            var authorizationHeaderValue = request.getHeader(authorizationHeaderKey);

            var tokenGrantProvider = tokenGrantProviderFactory
                    .getService(GrantType.fromValue(tokenRequestModel.grant_type));

            var tokenResponse = tokenGrantProvider.generateToken(authorizationHeaderValue, tokenRequestModel);

            if(tokenResponse.getError() != null){
                return new ResponseEntity<>(tokenResponse.getError(), HttpStatus.BAD_REQUEST);}
            else{
                return new ResponseEntity<>(tokenResponse.getResult(), HttpStatus.OK);
            }
        }
        catch(UnsupportedServiceTypeException e){
            return new ResponseEntity<>("invalid_grant", HttpStatus.BAD_REQUEST);
        }
    }
}
