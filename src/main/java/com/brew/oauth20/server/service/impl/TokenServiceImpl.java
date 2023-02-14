package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.service.TokenService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public String GenerateRandomTokenString() {
        int length = 32;
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.ints(length, 0, chars.length())
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }
}
