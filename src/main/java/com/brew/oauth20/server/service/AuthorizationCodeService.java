package com.brew.oauth20.server.service;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.ClientUser;

public interface AuthorizationCodeService {
    String createAuthorizationCode(String redirectUri, long expiresIn, ClientUser clientUser);

    ActiveAuthorizationCode getAuthorizationCode(String code, String redirectUri, boolean markAsUsed);
}
