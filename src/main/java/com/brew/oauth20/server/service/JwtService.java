package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.SignTokenOptions;

public interface JwtService {
    String signToken(SignTokenOptions signTokenOptions);
}
