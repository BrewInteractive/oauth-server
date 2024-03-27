package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.model.enums.OAuthError;
import com.brew.oauth20.server.utils.abstracts.BaseValidator;


public class ClientValidator extends BaseValidator<ClientModel> {
    public ClientValidator(ClientModel clientModel) {
        super(clientModel);
    }

    private static ValidationResultModel getErrorResponse(OAuthError error) {
        return new ValidationResultModel(false, error.getValue());
    }

    private static ValidationResultModel getSuccessResponse() {
        return new ValidationResultModel(true, null);
    }

    public ValidationResultModel validate(String responseType, String redirectUri, String scope) {
        if (!validateResponseType(responseType))
            return getErrorResponse(OAuthError.UNSUPPORTED_RESPONSE_TYPE);

        if (!validateRedirectUri(redirectUri))
            return getErrorResponse(OAuthError.INVALID_GRANT);

        if (scope != null && !scope.isBlank() && !validateScope(scope))
            return getErrorResponse(OAuthError.INVALID_SCOPE);

        return getSuccessResponse();
    }

    public ValidationResultModel validate(String grantType) {
        if (!validateGrantType(grantType))
            return getErrorResponse(OAuthError.UNSUPPORTED_GRANT_TYPE);

        return getSuccessResponse();
    }

    private boolean validateResponseType(String responseType) {
        return this.model.grantList().stream()
                .anyMatch(grantModel -> grantModel.responseType() == ResponseType.fromValue(responseType));
    }

    private boolean validateRedirectUri(String redirectUri) {
        return this.model.redirectUriList().stream()
                .anyMatch(redirectUriModel -> redirectUriModel.redirectUri().equals(redirectUri));
    }

    private boolean validateScope(String scope) {
        var authorizedScopes = this.model.scopeList().stream()
                .map(scopeModel -> scopeModel.scope().getScope())
                .toArray(String[]::new);
        var scopeValidator = new ScopeValidator(scope);
        return scopeValidator.validateScope(authorizedScopes);
    }

    private boolean validateGrantType(String grantType) {
        return this.model.grantList().stream()
                .anyMatch(grantModel -> grantModel.grantType() == GrantType.fromValue(grantType));
    }
}
