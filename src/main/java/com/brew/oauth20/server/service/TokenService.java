package com.brew.oauth20.server.service;

import java.security.NoSuchAlgorithmException;

public interface TokenService {
    String GenerateRandomTokenString() throws NoSuchAlgorithmException;
}

