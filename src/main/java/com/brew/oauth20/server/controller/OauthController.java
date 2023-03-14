package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.exception.UnsupportedResponseTypeException;
import com.brew.oauth20.server.model.AuthorizeRequest;
import com.brew.oauth20.server.model.ResponseType;
import com.brew.oauth20.server.provider.AuthorizeType.AuthorizeTypeProviderFactory;
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
import org.springframework.web.util.WebUtils;

import javax.management.ServiceNotFoundException;
import java.text.MessageFormat;
import java.util.UUID;

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
    public ResponseEntity authorizeGet(@Valid @ModelAttribute AuthorizeRequest authorizeRequest, BindingResult validationResult, HttpServletRequest request) {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            var redirectUri = authorizeRequest.redirect_uri;
            if (validationResult.hasErrors()) {
                var error = "invalid_request";
                String locationHeaderValue = MessageFormat.format("{0}{1}&error={2}", redirectUri, request.getQueryString(), error);
                responseHeaders.add("Location", locationHeaderValue);
                return new ResponseEntity(responseHeaders, HttpStatus.FOUND);
            }
            var client = clientService.getClient(UUID.fromString(authorizeRequest.client_id));
            var responseType = authorizeRequest.response_type;
            ResponseType responseTypeEnum = ResponseType.valueOf(responseType);
            var provider = authorizeTypeProviderFactory.getService(responseTypeEnum);
            var result = provider.Validate(request);
            var cookie = WebUtils.getCookie(request, "user_id");
            if (cookie.getValue() != null)
                authorizationCodeService.getAuthorizationCode("code", authorizeRequest.redirect_uri, true);
            else
                authorizationCodeService.createAuthorizationCode("subject??", authorizeRequest.redirect_uri, 12345, client.getId());
            String locationHeaderValue = MessageFormat.format("{0}{1}", redirectUri, request.getQueryString());
            responseHeaders.add("Location", locationHeaderValue);
            return new ResponseEntity(responseHeaders, HttpStatus.FOUND);
        } catch (ServiceNotFoundException e) {
            var redirectUri = authorizeRequest.redirect_uri;
            var queryString = request.getQueryString();
            var error = "unsupported_response_type";
            return createResult(redirectUri, queryString, error, HttpStatus.FOUND);
        } catch (UnsupportedResponseTypeException e) {
            var redirectUri = authorizeRequest.redirect_uri;
            var queryString = request.getQueryString();
            var error = "unsupported_response_type";
            return createResult(redirectUri, queryString, error, HttpStatus.FOUND);
        } catch (ClientNotFoundException e) {
            var redirectUri = authorizeRequest.redirect_uri;
            var queryString = request.getQueryString();
            var error = "unauthorized_client";
            return createResult(redirectUri, queryString, error, HttpStatus.FOUND);
        }
    }

    private ResponseEntity createResult(String redirectUri, String queryString, String error, HttpStatus httpStatus) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String locationHeaderValue = MessageFormat.format("{0}{1}&error={2}", redirectUri, queryString, error);
        responseHeaders.add("Location", locationHeaderValue);
        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
    }
}
