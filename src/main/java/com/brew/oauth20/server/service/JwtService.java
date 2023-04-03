package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.model.TokenModel;

public interface JwtService {
    TokenModel signToken(SignTokenOptions signTokenOptions);

    TokenModel signToken(SignTokenOptions signTokenOptions, String refreshToken);
}
