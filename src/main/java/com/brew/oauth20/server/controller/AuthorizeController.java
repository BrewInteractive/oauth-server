package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.component.UserCookieManager;
import com.brew.oauth20.server.controller.base.BaseController;
import com.brew.oauth20.server.data.ClientUser;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.ClientNotFoundException;
import com.brew.oauth20.server.exception.OAuthException;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.model.AuthorizeRequestModel;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.provider.authorizetype.BaseAuthorizeTypeProvider;
import com.brew.oauth20.server.service.AuthorizationCodeService;
import com.brew.oauth20.server.service.ClientUserService;
import com.brew.oauth20.server.service.factory.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.utils.validators.ScopeValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AuthorizeController extends BaseController {
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
        return clientUser.getClientUserScopes().stream().map(clientUserScope -> clientUserScope.getScope().getScope())
                .toArray(String[]::new);
    }

    private static boolean scopeExists(AuthorizeRequestModel authorizeRequest) {
        return StringUtils.hasText(authorizeRequest.getScope());
    }

    @NotNull
    private static String createQueryString(Map<String, String> requestParameters) {
        var queryStringBuilder = new StringBuilder();
        for (var entry : requestParameters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank())
                continue;
            if (!queryStringBuilder.isEmpty())
                queryStringBuilder.append("&");
            queryStringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return queryStringBuilder.toString();
    }

    private static Map<String, String> readRequestParameters(HttpServletRequest request) throws IOException {
        var inputStreamBytes = StreamUtils.copyToByteArray(request.getInputStream());
        return new ObjectMapper().readValue(inputStreamBytes, Map.class);
    }

    private static Map<String, String> convertToMap(Map<String, String[]> parameterMap) {
        return parameterMap.entrySet().stream()
                .filter(entry -> entry.getValue().length > 0 && entry.getValue()[0] != null && !entry.getValue()[0].isBlank())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
    }

    @GetMapping(value = "/oauth/authorize")
    public ResponseEntity<String> authorizeGet(
            @Valid @ModelAttribute("authorizeRequest") AuthorizeRequestModel authorizeRequest,
            BindingResult validationResult,
            HttpServletRequest request,
            HttpServletResponse response) {
        var requestParameters = convertToMap(request.getParameterMap());
        return authorize(authorizeRequest, validationResult, request, convertToParameters(requestParameters), convertToRedirectUriParameters(requestParameters));
    }

    @PostMapping(value = "/oauth/authorize")
    public ResponseEntity<String> authorizePost(@Valid @RequestBody AuthorizeRequestModel authorizeRequest,
                                                BindingResult validationResult,
                                                HttpServletRequest request) throws IOException {

        var requestParameters = readRequestParameters(request);
        return authorize(authorizeRequest, validationResult, request, convertToParameters(requestParameters), convertToRedirectUriParameters(requestParameters));
    }

    private ResponseEntity<String> authorize(AuthorizeRequestModel authorizeRequest,
                                             BindingResult validationResult,
                                             HttpServletRequest request,
                                             String parameters,
                                             String redirectUriParameters) {
        try {
            validateRequest(validationResult);
            validateAuthorizeType(authorizeRequest);

            /* check user cookie */
            var userIdOptional = userCookieManager.getUser(request);
            /* not logged-in user redirect login signup */
            if (userIdOptional.isEmpty())
                return redirectToLoginSignup(parameters);

            var clientUser = obtainClientUser(authorizeRequest, userIdOptional.get());
            if (Boolean.TRUE.equals(consentRequired(authorizeRequest, clientUser)))
                return redirectToConsent(parameters);

            return redirectToRedirectUri(authorizeRequest, redirectUriParameters, clientUser);
        } catch (OAuthException e) {
            logger.error(e.getMessage(), e);
            return generateErrorResponse(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return generateErrorResponse(OAuthError.SERVER_ERROR.getValue());
        }
    }

    private ClientUser obtainClientUser(AuthorizeRequestModel authorizeRequest, String userId) {
        try {
            return clientUserService.getOrCreate(authorizeRequest.getClient_id(), userId);
        } catch (ClientNotFoundException e) {
            throw new OAuthException(OAuthError.INVALID_CLIENT);
        }
    }

    private Boolean consentRequired(AuthorizeRequestModel authorizeRequest,
                                    ClientUser clientUser) {
        if (scopeExists(authorizeRequest)) {
            var scopeValidator = new ScopeValidator(authorizeRequest.getScope());
            return !scopeValidator.validateScope(getAuthorizedScopes(clientUser));
        }
        return false;
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

    private void validateAuthorizeType(AuthorizeRequestModel authorizeRequest) {
        var authorizeTypeProvider = createAuthorizeTypeProvider(authorizeRequest);
        authorizeTypeProvider.validate(
                authorizeRequest.getClient_id(),
                authorizeRequest.getRedirect_uri(),
                authorizeRequest.getScope());
    }

    @NotNull
    private BaseAuthorizeTypeProvider createAuthorizeTypeProvider(AuthorizeRequestModel authorizeRequest) {
        BaseAuthorizeTypeProvider authorizeTypeProvider;
        try {
            if (authorizeRequest.getResponse_type().equals("token")) // Unsupported right now
                throw new OAuthException(OAuthError.UNSUPPORTED_RESPONSE_TYPE);

            authorizeTypeProvider = authorizeTypeProviderFactory.getService(ResponseType.fromValue(authorizeRequest.getResponse_type()));
            if (authorizeTypeProvider == null)
                throw new OAuthException(OAuthError.UNSUPPORTED_RESPONSE_TYPE);
        } catch (UnsupportedServiceTypeException e) {
            throw new OAuthException(OAuthError.UNSUPPORTED_RESPONSE_TYPE);
        }
        return authorizeTypeProvider;
    }

    @NotNull
    private ResponseEntity<String> redirectToRedirectUri(AuthorizeRequestModel authorizeRequest,
                                                         String redirectUriParameters,
                                                         ClientUser clientUser) {

        var expiresMs = env.getProperty("oauth.authorization_code_expires_ms",
                DEFAULT_AUTHORIZATION_CODE_EXPIRES_MS);
        var code = authorizationCodeService.createAuthorizationCode(
                authorizeRequest.getRedirect_uri(),
                Long.parseLong(expiresMs),
                clientUser,
                authorizeRequest.getScope());

        /* logged-in user redirect with authorization code */
        return generateSuccessResponse(
                code,
                authorizeRequest.getRedirect_uri(),
                redirectUriParameters);

    }

    private String convertToParameters(Map<String, String> requestParameters) {
        return createQueryString(requestParameters);
    }

    private String convertToRedirectUriParameters(Map<String, String> requestParameters) {
        var sanitizedMap = requestParameters.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("response_type"))
                .filter(entry -> !entry.getKey().equals("redirect_uri"))
                .filter(entry -> !entry.getKey().equals("client_id"))
                .filter(entry -> !entry.getKey().equals("scope"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return createQueryString(sanitizedMap);
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

    private ResponseEntity<String> generateSuccessResponse(String code, String redirectUri, String parameters) {
        var location = UriComponentsBuilder.fromUriString(redirectUri)
                .query(parameters)
                .queryParam("code", code)
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
