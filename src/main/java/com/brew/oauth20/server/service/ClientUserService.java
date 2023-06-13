package com.brew.oauth20.server.service;

import java.util.UUID;

public interface ClientUserService {
    UUID create(String clientId, Long userId);
}
