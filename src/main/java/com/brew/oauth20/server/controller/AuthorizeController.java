package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.AuthorizeRequestModel;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class AuthorizeController {
    public static final String DEFAULT_AUTHORIZATION_CODE_EXPIRES_MS = "300000";
    private final CookieService cookieService;
    private final AuthorizationCodeService authorizationCodeService;
    private final AuthorizeTypeProviderFactory authorizeTypeProviderFactory;
    private final String userIdCookieKey;
    @Value("${oauth.login_signup_endpoint}")
    private String loginSignupEndpoint;
    @Autowired
    private Environment env;

    public AuthorizeController(CookieService cookieService,
                               AuthorizeTypeProviderFactory authorizeTypeProviderFactory,
                               AuthorizationCodeService authorizationCodeService) {
        this.cookieService = cookieService;
        this.authorizeTypeProviderFactory = authorizeTypeProviderFactory;
        this.authorizationCodeService = authorizationCodeService;
        this.userIdCookieKey = "SESSION_ID";
    }

    @GetMapping(value = "/oauth/authorize")
    public ResponseEntity<String> authorizeGet(
            @Valid @ModelAttribute("authorizeRequest") AuthorizeRequestModel authorizeRequest,
            BindingResult validationResult,
            HttpServletRequest request) {
        return authorize(authorizeRequest, validationResult, request, request.getQueryString());
    }

    @PostMapping(value = "/oauth/authorize")
    public ResponseEntity<String> authorizePost(@Valid @RequestBody AuthorizeRequestModel authorizeRequest,
                                                BindingResult validationResult,
                                                HttpServletRequest request) {
        return authorize(authorizeRequest, validationResult, request, convertToParameters(authorizeRequest));
    }

    private ResponseEntity<String> authorize(AuthorizeRequestModel authorizeRequest,
                                             BindingResult validationResult,
                                             HttpServletRequest request,
                                             String parameters) {
        try {
            /* request parameters validation */
            if (validationResult.hasErrors())
                return generateErrorResponse("invalid_request", parameters, authorizeRequest.getRedirect_uri());


            /* authorize type validator */
            var authorizeTypeProvider = authorizeTypeProviderFactory
                    .getService(ResponseType.fromValue(authorizeRequest.getResponse_type()));

            var authorizeTypeValidationResult = authorizeTypeProvider.validate(authorizeRequest.getClient_id(),
                    authorizeRequest.getRedirect_uri());

            if (Boolean.FALSE.equals(authorizeTypeValidationResult.getResult()))
                return generateErrorResponse(authorizeTypeValidationResult.getError(), parameters,
                        authorizeRequest.getRedirect_uri());


            /* user cookie and authorization code */
            var userCookie = cookieService.getCookie(request, userIdCookieKey);

            /* not logged-in user redirect login signup */
            if (userCookie == null) {
                if (loginSignupEndpoint.isBlank())
                    throw new IllegalStateException("LOGIN_SIGNUP_ENDPOINT is not set in the environment variables");

                return generateLoginResponse(loginSignupEndpoint, parameters);
            }

            var expiresMs = env.getProperty("oauth.authorization_code_expires_ms", DEFAULT_AUTHORIZATION_CODE_EXPIRES_MS);

            var code = authorizationCodeService.createAuthorizationCode(Long.parseLong(userCookie),
                    authorizeRequest.getRedirect_uri(),
                    Long.parseLong(expiresMs),
                    authorizeRequest.getClient_id());

            /* logged-in user redirect with authorization code */
            return generateSuccessResponse(code, authorizeRequest.getRedirect_uri(), parameters);
        } catch (UnsupportedServiceTypeException e) {
            return generateErrorResponse("unsupported_response_type", parameters,
                    authorizeRequest.redirect_uri);
        } catch (Exception e) {
            return generateErrorResponse("server_error", parameters, authorizeRequest.getRedirect_uri());
        }
    }

    private String convertToParameters(AuthorizeRequestModel authorizeRequest) {
        var queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("response_type=")
                .append(URLEncoder.encode(authorizeRequest.getResponse_type(), StandardCharsets.UTF_8))
                .append("&redirect_uri=" + URLEncoder.encode(authorizeRequest.getRedirect_uri(), StandardCharsets.UTF_8))
                .append("&client_id=" + URLEncoder.encode(authorizeRequest.getClient_id(), StandardCharsets.UTF_8));
        if (!authorizeRequest.getState().isBlank())
            queryStringBuilder.append("&state=" + URLEncoder.encode(authorizeRequest.getState(), StandardCharsets.UTF_8));
        return queryStringBuilder.toString();
    }

    private ResponseEntity<String> generateErrorResponse(String error, String parameters, String redirectUri) {
        var headers = new HttpHeaders();
        if (redirectUri != null) {
            var location = UriComponentsBuilder.fromUriString(redirectUri)
                    .query(parameters)
                    .queryParam("error", error)
                    .build()
                    .toUri();
            headers.setLocation(location);
        }
        return new ResponseEntity<>(error, headers, HttpStatus.FOUND);
    }

    private ResponseEntity<String> generateLoginResponse(String loginSignupEndpoint, String parameters) {
        var location = UriComponentsBuilder.fromUriString(loginSignupEndpoint)
                .query(parameters)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private ResponseEntity<String> generateSuccessResponse(String code, String redirectUri, String parameters) {
        var location = UriComponentsBuilder.fromUriString(redirectUri)
                .query(parameters)
                .queryParam("code", code)
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
