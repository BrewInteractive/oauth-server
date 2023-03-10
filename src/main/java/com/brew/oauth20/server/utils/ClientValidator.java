package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.ValidationResultModel;
import com.brew.oauth20.server.utils.interfaces.Validator;

import java.util.ArrayList;

public class ClientValidator implements Validator<ClientModel> {

    private final String responseType;
    private final ArrayList<String> redirectUriList;

    public ClientValidator(String responseType, ArrayList<String> redirectUriList) {
        this.responseType = responseType;
        this.redirectUriList = redirectUriList;
    }

    @Override
    public ValidationResultModel validate(ClientModel client) {
        return new ValidationResultModel(true, null);
    }
}
