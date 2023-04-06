package com.brew.oauth20.server.model;

public class ValidationResultModel extends BaseResultModel<Boolean> {
    public ValidationResultModel(Boolean result, String error) {
        super(result, error);
    }
}

