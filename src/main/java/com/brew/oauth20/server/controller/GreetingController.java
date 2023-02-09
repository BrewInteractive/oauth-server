package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.model.GreetingModel;
import com.brew.oauth20.server.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService _greetingService){
        greetingService = _greetingService;
    }

    @GetMapping("/greeting")
    public GreetingModel greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return greetingService.getGreetingModel(name);
    }
}