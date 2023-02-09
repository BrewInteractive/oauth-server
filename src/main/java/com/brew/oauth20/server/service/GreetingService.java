package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.GreetingModel;

public interface GreetingService {
    GreetingModel getGreetingModel(String name);
}
