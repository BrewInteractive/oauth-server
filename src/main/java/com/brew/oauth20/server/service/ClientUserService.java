package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.ClientUser;

public interface ClientUserService {
    ClientUser create(String clientId, String userId);
}
