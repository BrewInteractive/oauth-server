package com.brew.oauth20.server.utils;

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

    public ValidationResultModel validate(String responseType, String redirectUri) {
        if (!validateResponseType(responseType))
            return getErrorResponse();

        if (!validateRedirectUri(redirectUri))
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
}
