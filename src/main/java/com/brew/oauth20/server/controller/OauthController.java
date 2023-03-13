package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.model.AuthorizeRequest;
import com.brew.oauth20.server.model.ResponseType;
import com.brew.oauth20.server.provider.AuthorizeType.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OauthController {
    private final AuthorizationCodeService authorizationCodeService;
    private final ClientService clientService;
    private final AuthorizeTypeProviderFactory authorizeTypeProviderFactory;

    public OauthController(AuthorizationCodeService _authorizationCodeService, ClientService _clientService, AuthorizeTypeProviderFactory _authorizeTypeProviderFactory) {

        this.authorizationCodeService = _authorizationCodeService;
        this.clientService = _clientService;
        this.authorizeTypeProviderFactory = _authorizeTypeProviderFactory;
    }

    @RequestMapping(value = "/oauth/authorize", method = RequestMethod.GET)
    public ResponseEntity<String> authorizeGet(@Valid @ModelAttribute AuthorizeRequest authorizeRequest, BindingResult validationResult, HttpServletRequest request) {

        ResponseType responseType = ResponseType.valueOf(authorizeRequest.response_type);

        var provider = authorizeTypeProviderFactory.getService(responseType);

        var result = provider.Validate(request);

        return new ResponseEntity(result.getSecond() + ", " + validationResult.hasErrors(), HttpStatus.OK);
    }
}
