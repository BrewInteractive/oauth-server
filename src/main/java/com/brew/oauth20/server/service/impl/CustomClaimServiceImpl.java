package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.HookModel;
import com.brew.oauth20.server.service.CustomClaimService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomClaimServiceImpl implements CustomClaimService {
    @Override
    public Map<String, Object> getCustomClaims(HookModel customClaimHook, String userId) {
        return Collections.emptyMap();
    }
}
