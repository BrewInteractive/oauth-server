package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.utils.interfaces.Validator;


public class ClientValidator implements Validator<ClientModel> {

    private final String responseType;
    private final String redirectUri;

    public ClientValidator(String responseType, String redirectUri) {
        this.responseType = responseType;
        this.redirectUri = redirectUri;
    }

    private static ValidationResultModel getErrorResponse() {
        return new ValidationResultModel(false, "unauthorized_client");
    }

    private static ValidationResultModel getSuccessResponse() {
        return new ValidationResultModel(true, null);
    }

    @Override
    public ValidationResultModel validate(ClientModel client) {
        if (!validateResponseType(client))
            return getErrorResponse();

        if (!validateRedirectUri(client))
            return getErrorResponse();

        return getSuccessResponse();
    }

    private boolean validateResponseType(ClientModel client) {
        return client.grantList().stream()
                .anyMatch(grantModel -> grantModel.responseType() == ResponseType.fromValue(this.responseType));
    }

    private boolean validateRedirectUri(ClientModel client) {
        return client.redirectUriList().stream()
                .anyMatch(redirectUriModel -> redirectUriModel.redirectUri().equals(this.redirectUri));
    }
}
