package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.AuthorizeRequestModel;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.UserCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthorizeController {
    public static final String DEFAULT_AUTHORIZATION_CODE_EXPIRES_MS = "300000";
    private final UserCookieService userCookieService;
    private final AuthorizationCodeService authorizationCodeService;
    private final AuthorizeTypeProviderFactory authorizeTypeProviderFactory;
    private final String userIdCookieKey;
    private final String locationHeaderKey;
    @Autowired
    private Environment env;

    public AuthorizeController(UserCookieService userCookieService,
                               AuthorizeTypeProviderFactory authorizeTypeProviderFactory,
                               AuthorizationCodeService authorizationCodeService) {
        this.userCookieService = userCookieService;
        this.authorizeTypeProviderFactory = authorizeTypeProviderFactory;
        this.authorizationCodeService = authorizationCodeService;
        this.userIdCookieKey = "SESSION_ID";
        this.locationHeaderKey = "Location";
    }

    @GetMapping(value = "/oauth/authorize")
    public ResponseEntity<String> authorizeGet(
            @Valid @ModelAttribute("authorizeRequest") AuthorizeRequestModel authorizeRequest,
            BindingResult validationResult,
            HttpServletRequest request) {
        return authorize(authorizeRequest, validationResult, request);
    }

    @PostMapping(value = "/oauth/authorize")
    public ResponseEntity<String> authorizePost(@Valid @RequestBody AuthorizeRequestModel authorizeRequest,
                                                BindingResult validationResult,
                                                HttpServletRequest request) {
        return authorize(authorizeRequest, validationResult, request);
    }


    private ResponseEntity<String> authorize(AuthorizeRequestModel authorizeRequest,
                                             BindingResult validationResult,
                                             HttpServletRequest request) {
        try {
            String queryString = request.getQueryString();

            /* request parameters validation */
            if (validationResult.hasErrors()) {
                return generateErrorResponse("invalid_request", queryString, authorizeRequest.redirect_uri);
            }

            /* authorize type validator */
            var authorizeTypeProvider = authorizeTypeProviderFactory
                    .getService(ResponseType.fromValue(authorizeRequest.response_type));

            var authorizeTypeValidationResult = authorizeTypeProvider.validate(authorizeRequest.client_id,
                    authorizeRequest.redirect_uri);


            if (Boolean.FALSE.equals(authorizeTypeValidationResult.getResult())) {
                return generateErrorResponse(authorizeTypeValidationResult.getError(), queryString, authorizeRequest.redirect_uri);
            }

            /* user cookie and authorization code */
            var userCookie = userCookieService.getUserCookie(request, userIdCookieKey);

            /* not logged-in user redirect login signup */
            if (userCookie == null) {
                return generateLoginResponse(authorizeRequest.redirect_uri);
            }

            var expiresMs = env.getProperty("AUTHORIZATION_CODE_EXPIRES_MS", DEFAULT_AUTHORIZATION_CODE_EXPIRES_MS);

            var code = authorizationCodeService.createAuthorizationCode(Long.parseLong(userCookie),
                    authorizeRequest.redirect_uri,
                    Long.parseLong(expiresMs),
                    authorizeRequest.client_id);

            /* logged-in user redirect with authorization code */
            return generateSuccessResponse(code, authorizeRequest.redirect_uri);
        } catch (UnsupportedServiceTypeException e) {
            return generateErrorResponse("unsupported_response_type", request.getQueryString(),
                    authorizeRequest.redirect_uri);
        } catch (Exception e) {
            return generateErrorResponse("server_error", request.getQueryString(), authorizeRequest.redirect_uri);
        }
    }

    private ResponseEntity<String> generateErrorResponse(String error, String queryString, String redirectUri) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String locationHeader = redirectUri + (queryString == null ? "" : ("?" + queryString)) + ("&error=" + error);
        if (redirectUri != null)
            responseHeaders.set(locationHeaderKey, locationHeader);
        return new ResponseEntity<>(error, responseHeaders, HttpStatus.FOUND);
    }

    private ResponseEntity<String> generateLoginResponse(String redirectUri) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String loginSignupRoute = redirectUri + "/login";
        responseHeaders.set(locationHeaderKey, loginSignupRoute);
        return new ResponseEntity<>(responseHeaders, HttpStatus.FOUND);
    }

    private ResponseEntity<String> generateSuccessResponse(String code, String redirectUri) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String successRoute = redirectUri + "?code=" + code
                + "?locale=" + "fr"
                + "?state=" + "abc123"
                + "?userState=" + "Authenticated";

        responseHeaders.set(locationHeaderKey, successRoute);
        return new ResponseEntity<>(responseHeaders, HttpStatus.FOUND);
    }
}
