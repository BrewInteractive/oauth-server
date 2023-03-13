package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.model.AuthorizeRequest;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class OauthController {
    private final AuthorizationCodeService authorizationCodeService;
    private final ClientService clientService;

    public OauthController(AuthorizationCodeService _authorizationCodeService, ClientService _clientService) {

        this.authorizationCodeService = _authorizationCodeService;
        this.clientService = _clientService;
    }

    @RequestMapping(value = "/oauth/authorize", method = RequestMethod.GET)
    public ResponseEntity<String> authorizeGet(@Valid @ModelAttribute AuthorizeRequest authorizeRequest, BindingResult validationResult, HttpServletRequest request) {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            String locationHeader = authorizeRequest.redirect_uri + "?" + (request.getQueryString() == null ? "" : request.getQueryString());
            if (validationResult.hasErrors()) {
                String error = "invalid_request";
                String errorPrefix = (request.getQueryString() == null ? "" : "&error=" + error);
                responseHeaders.set("Location", locationHeader + errorPrefix);
                return new ResponseEntity(error, responseHeaders, HttpStatus.BAD_REQUEST);
            }
            Client client = clientService.getClient(UUID.fromString(authorizeRequest.client_id));
            responseHeaders.set("Location", locationHeader);
            return new ResponseEntity(responseHeaders, HttpStatus.OK);
        } catch (ClientNotFoundException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            String locationHeader = authorizeRequest.redirect_uri + "?" + (request.getQueryString() == null ? "" : request.getQueryString());
            String error = "unauthorized_client";
            String errorPrefix = (request.getQueryString() == null ? "" : "&error=" + error);
            responseHeaders.set("Location", locationHeader + errorPrefix);
            return new ResponseEntity(error, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }
}
