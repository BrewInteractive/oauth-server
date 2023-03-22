package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.AuthorizeRequestModel;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.UserCookieService;
import com.brew.oauth20.server.utils.UriUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizeController {
    private final UserCookieService userCookieService;
    private final AuthorizationCodeService authorizationCodeService;
    private final AuthorizeTypeProviderFactory authorizeTypeProviderFactory;
    private final String userIdCookieKey;
    private final String locationHeaderKey;

    public AuthorizeController(UserCookieService userCookieService, AuthorizeTypeProviderFactory authorizeTypeProviderFactory, AuthorizationCodeService authorizationCodeService) {
        this.userCookieService = userCookieService;
        this.authorizeTypeProviderFactory = authorizeTypeProviderFactory;
        this.authorizationCodeService = authorizationCodeService;
        this.userIdCookieKey = "SESSION_ID";
        this.locationHeaderKey = "Location";
    }

    @GetMapping(value = "/oauth/authorize")
    public ResponseEntity<String> get(@Valid @ModelAttribute("authorizeRequestModel") AuthorizeRequestModel authorizeRequestModel, BindingResult validationResult, HttpServletRequest request) {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            String queryString = request.getQueryString();

            if (!UriUtils.isValidUrl(authorizeRequestModel.redirect_uri))
                return new ResponseEntity<>("invalid_request", HttpStatus.BAD_REQUEST);

            /*request parameters validation*/
            if (validationResult.hasErrors()) {
                return generateRedirectErrorResponse("invalid_request", queryString, authorizeRequestModel.redirect_uri);
            }

            /*authorize type validator*/
            var authorizeTypeProvider = authorizeTypeProviderFactory.getService(ResponseType.fromValue(authorizeRequestModel.response_type));

            var authorizeTypeValidationResult = authorizeTypeProvider.validate(authorizeRequestModel.client_id, authorizeRequestModel.redirect_uri);

            if (Boolean.FALSE.equals(authorizeTypeValidationResult.result())) {
                return generateRedirectErrorResponse(authorizeTypeValidationResult.error(), queryString, authorizeRequestModel.redirect_uri);
            }

            /*user cookie and authorization code*/
            var userCookie = userCookieService.getUserCookie(request, userIdCookieKey);

            if (userCookie == null) {
                String loginSignupRoute = authorizeRequestModel.redirect_uri + "/login";
                responseHeaders.set(locationHeaderKey, loginSignupRoute);
                return new ResponseEntity<>(responseHeaders, HttpStatus.TEMPORARY_REDIRECT);
            }

            var code = authorizationCodeService.createAuthorizationCode(Long.parseLong(userCookie), authorizeRequestModel.redirect_uri, 100, authorizeRequestModel.client_id);

            String successRoute = authorizeRequestModel.redirect_uri + "?code=" + code
                    + "?locale=" + "fr"
                    + "?state=" + "abc123"
                    + "?userState=" + "Authenticated";

            responseHeaders.set(locationHeaderKey, successRoute);
            return new ResponseEntity<>(responseHeaders, HttpStatus.FOUND);
        } catch (UnsupportedServiceTypeException e) {
            return generateRedirectErrorResponse("unsupported_response_type", request.getQueryString(), authorizeRequestModel.redirect_uri);
        } catch (Exception e) {
            return generateRedirectErrorResponse("server_error", request.getQueryString(), authorizeRequestModel.redirect_uri);
        }
    }

    private ResponseEntity<String> generateRedirectErrorResponse(String error, String queryString, String redirectUri) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String errorPrefix = (queryString == null ? "" : ("&error=" + error));
        String locationHeader = redirectUri + (queryString == null ? "" : ("?" + queryString));
        responseHeaders.set(locationHeaderKey, locationHeader + errorPrefix);
        return new ResponseEntity<>(error, responseHeaders, HttpStatus.FOUND);
    }
}
