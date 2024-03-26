package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.HookModel;

import java.util.Map;

public interface CustomClaimService {
    Map<String, Object> getCustomClaims(HookModel customClaimHook, String userId);
}
