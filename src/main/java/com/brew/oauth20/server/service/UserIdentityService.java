package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.UserIdentityInfoModel;

public interface UserIdentityService {
    UserIdentityInfoModel getUserIdentity(String accessToken);
}
