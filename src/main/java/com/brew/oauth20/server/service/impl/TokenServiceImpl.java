package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.TokenModel;
import com.brew.oauth20.server.service.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Override
    public TokenModel generateToken(ClientModel client, Long userId, String state) {
        return null;
    }

    @Override
    public TokenModel generateToken(ClientModel client, Long userId, String state, String refreshToken) {
        return null;
    }
}
