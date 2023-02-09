package com.brew.oauth20.server.service.impl;

import com.brew.oauth20.server.model.GreetingModel;
import com.brew.oauth20.server.service.GreetingService;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GreetingServiceImpl implements GreetingService {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    @Override
    public GreetingModel getGreetingModel(String name) {
        return new GreetingModel(counter.incrementAndGet(), String.format(template, name));
    }
}
