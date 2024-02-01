package com.brew.oauth20.server.service;

import java.util.Map;

public interface UserIdentityService {
    Map<String, Object> getUserIdentityInfo(String accessToken);
}
