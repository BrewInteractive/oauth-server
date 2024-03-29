package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.component.UserCookieManager;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.AuthorizeRequestModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.ClientUserService;
import com.brew.oauth20.server.service.factory.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.utils.validators.ScopeValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
public class AuthorizeController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizeController.class);
    private static final String DEFAULT_AUTHORIZATION_CODE_EXPIRES_MS = "300000";
    private final UserCookieManager userCookieManager;
    private final AuthorizationCodeService authorizationCodeService;
    private final AuthorizeTypeProviderFactory authorizeTypeProviderFactory;
    private final ClientUserService clientUserService;
    private final Environment env;

    @Value("${oauth.login_signup_endpoint}")
    private String loginSignupEndpoint;

    @Value("${oauth.error_page_url}")
    private String errorPageUrl;

    @Value("${oauth.consent_endpoint}")
    private String consentEndpoint;

    @Autowired
    public AuthorizeController(UserCookieManager userCookieManager,
                               AuthorizationCodeService authorizationCodeService,
                               AuthorizeTypeProviderFactory authorizeTypeProviderFactory,
                               ClientUserService clientUserService,
                               Environment env) {
        this.userCookieManager = userCookieManager;
        this.authorizationCodeService = authorizationCodeService;
        this.authorizeTypeProviderFactory = authorizeTypeProviderFactory;
        this.clientUserService = clientUserService;
        this.env = env;
    }

    @NotNull
    private static String[] getAuthorizedScopes(ClientUser clientUser) {
        return clientUser.getClientUserScopes().stream().map(clientUserScope -> clientUserScope.getScope().getScope()).toArray(String[]::new);
    }

    private static boolean scopeExists(AuthorizeRequestModel authorizeRequest) {
        return authorizeRequest.getScope() != null && !authorizeRequest.getScope().isBlank();
    }

    private static boolean stateExists(AuthorizeRequestModel authorizeRequest) {
        return authorizeRequest.getState() != null && !authorizeRequest.getState().isBlank();
    }

    @GetMapping(value = "/oauth/authorize")
    public ResponseEntity<String> authorizeGet(
            @Valid @ModelAttribute("authorizeRequest") AuthorizeRequestModel authorizeRequest,
            BindingResult validationResult,
            HttpServletRequest request,
            HttpServletResponse response) {
        return authorize(authorizeRequest, validationResult, request,
                URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8));
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
            var errorResponse = validateClientRequest(authorizeRequest, validationResult);
            if (errorResponse != null) {
                logger.error("invalid_request");
                return errorResponse;
            }

            /* check user cookie */
            var userIdOptional = userCookieManager.getUser(request);

            /* not logged-in user redirect login signup */
            if (userIdOptional.isEmpty())
                return redirectToLoginSignup(parameters);

            var clientUser = clientUserService.getOrCreate(authorizeRequest.getClient_id(), userIdOptional.get());
            var consentResponse = validateClientUserConsent(authorizeRequest, parameters, clientUser);
            if (consentResponse != null) return consentResponse;

            return redirectToRedirectUri(authorizeRequest, parameters, clientUser);
        } catch (UnsupportedServiceTypeException e) {
            logger.error(e.getMessage(), e);
            return generateErrorResponse("unsupported_response_type");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return generateErrorResponse("server_error");
        }
    }

    @Nullable
    private ResponseEntity<String> validateClientUserConsent(AuthorizeRequestModel authorizeRequest, String parameters, ClientUser clientUser) {
        if (scopeExists(authorizeRequest)) {
            var scopeValidator = new ScopeValidator(authorizeRequest.getScope());
            if (!scopeValidator.validateScope(getAuthorizedScopes(clientUser)))
                return redirectToConsent(parameters);
        }
        return null;
    }

    @Nullable
    private ResponseEntity<String> validateClientRequest(AuthorizeRequestModel authorizeRequest, BindingResult validationResult) {
        /* request parameters validation */
        if (validationResult.hasErrors())
            return generateErrorResponse("invalid_request");

        var authorizeTypeValidationResult = validateAuthorizeType(authorizeRequest);
        if (Boolean.FALSE.equals(authorizeTypeValidationResult.getResult()))
            return generateErrorResponse(authorizeTypeValidationResult.getError());
        return null;
    }

    @NotNull
    private ResponseEntity<String> redirectToConsent(String parameters) {
        validateConsentEndpoint();
        return generateConsentResponse(consentEndpoint, parameters);
    }

    @NotNull
    private ResponseEntity<String> redirectToLoginSignup(String parameters) {
        validateLoginSignupEndpoint();
        return generateLoginSignupResponse(loginSignupEndpoint, parameters);
    }

    private void validateConsentEndpoint() {
        if (consentEndpoint.isBlank())
            throw new IllegalStateException("CONSENT_ENDPOINT is not set in the environment variables");
    }

    private void validateLoginSignupEndpoint() {
        if (loginSignupEndpoint.isBlank())
            throw new IllegalStateException("LOGIN_SIGNUP_ENDPOINT is not set in the environment variables");
    }

    private void validateErrorPageUrl() {
        if (errorPageUrl.isBlank())
            throw new IllegalStateException("ERROR_PAGE_URL is not set in the environment variables");
    }

    private ValidationResultModel validateAuthorizeType(AuthorizeRequestModel authorizeRequest) {
        /* authorize type validator */
        var authorizeTypeProvider = authorizeTypeProviderFactory
                .getService(ResponseType.fromValue(authorizeRequest.getResponse_type()));

        return authorizeTypeProvider.validate(authorizeRequest.getClient_id(), authorizeRequest.getRedirect_uri(), authorizeRequest.getScope());
    }

    @NotNull
    private ResponseEntity<String> redirectToRedirectUri(AuthorizeRequestModel authorizeRequest, String parameters, ClientUser clientUser) {
        if (authorizeRequest.getResponse_type().equals("token"))
            throw new UnsupportedServiceTypeException();
        else {
            var expiresMs = env.getProperty("oauth.authorization_code_expires_ms", DEFAULT_AUTHORIZATION_CODE_EXPIRES_MS);
            var code = authorizationCodeService.createAuthorizationCode(authorizeRequest.getRedirect_uri(),
                    Long.parseLong(expiresMs),
                    clientUser,
                    authorizeRequest.getScope());

            /* logged-in user redirect with authorization code */
            return generateSuccessResponse(code, authorizeRequest.getRedirect_uri(), parameters, clientUser.getUserId());
        }
    }

    private String convertToParameters(AuthorizeRequestModel authorizeRequest) {
        var queryStringBuilder = new StringBuilder();
        queryStringBuilder
                .append("response_type=").append(authorizeRequest.getResponse_type())
                .append("&redirect_uri=").append(authorizeRequest.getRedirect_uri())
                .append("&client_id=").append(authorizeRequest.getClient_id());
        if (scopeExists(authorizeRequest))
            queryStringBuilder.append("&scope=").append(authorizeRequest.getScope());
        if (stateExists(authorizeRequest))
            queryStringBuilder.append("&state=").append(authorizeRequest.getState());
        return queryStringBuilder.toString();
    }

    private ResponseEntity<String> generateErrorResponse(String error) {
        validateErrorPageUrl();
        URI location = UriComponentsBuilder.fromUriString(errorPageUrl)
                    .queryParam("error", error)
                    .build()
                    .toUri();
        return createRedirectResponse(error, location);
    }

    private ResponseEntity<String> generateLoginSignupResponse(String loginSignupEndpoint, String parameters) {
        var location = UriComponentsBuilder.fromUriString(loginSignupEndpoint)
                .query(parameters)
                .build()
                .toUri();
        return createRedirectResponse("", location);
    }

    private ResponseEntity<String> generateConsentResponse(String consentEndpoint, String parameters) {
        var location = UriComponentsBuilder.fromUriString(consentEndpoint)
                .query(parameters)
                .build()
                .toUri();
        return createRedirectResponse("", location);
    }

    private ResponseEntity<String> generateSuccessResponse(String code, String redirectUri, String parameters, String userId) {
        var location = UriComponentsBuilder.fromUriString(redirectUri)
                .query(parameters)
                .queryParam("code", code)
                .queryParam("user_id", userId)
                .build()
                .toUri();
        return createRedirectResponse("", location);
    }

    private ResponseEntity<String> createRedirectResponse(String body, URI location) {
        var headers = new HttpHeaders();
        if (location != null) {
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setLocation(location);
        }
        return new ResponseEntity<>(body, headers, HttpStatus.FOUND);
    }
}
