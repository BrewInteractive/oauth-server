package com.brew.oauth20.server.model;

public class TokenResultModel extends BaseResultModel<TokenModel> {
    public TokenResultModel(TokenModel token, String error) {
        super(token, error);
    }
}
