package com.brew.oauth20.server;

import com.brew.oauth20.server.controller.GreetingController;
import com.brew.oauth20.server.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationTests {
    @Autowired
    private GreetingController greetingController;
    @Autowired
    private GreetingService greetingService;

    @Test
    void contextLoads() {
        assertThat(greetingController).isNotNull();
        assertThat(greetingService).isNotNull();
    }
}
