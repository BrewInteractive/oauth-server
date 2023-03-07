package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String authorize() {
        return "asd";
    }
}
