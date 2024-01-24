package com.brew.oauth20.server.utils.validators;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.utils.abstracts.BaseValidator;


public class ClientValidator extends BaseValidator<ClientModel> {
    public ClientValidator(ClientModel clientModel) {
        super(clientModel);
    }

    private static ValidationResultModel getErrorResponse() {
        return new ValidationResultModel(false, "unauthorized_client");
    }

    private static ValidationResultModel getSuccessResponse() {
        return new ValidationResultModel(true, null);
    }

    public ValidationResultModel validate(String responseType, String redirectUri, String scope) {
        if (!validateResponseType(responseType))
            return getErrorResponse();

        if (!validateRedirectUri(redirectUri))
            return getErrorResponse();

        if (scope != null && !scope.isBlank() && !validateScope(scope))
            return getErrorResponse();

        return getSuccessResponse();
    }


    public ValidationResultModel validate(String grantType) {
        if (!validateGrantType(grantType))
            return getErrorResponse();

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
