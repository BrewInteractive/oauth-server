package com.brew.oauth20.server.Controller;


import com.brew.oauth20.server.controller.GreetingController;
import com.brew.oauth20.server.model.GreetingModel;
import com.brew.oauth20.server.service.GreetingService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;



@SpringBootTest
public class GreetingControllerTest {
    @MockBean
    private GreetingService service;
    private static Faker faker;

    @BeforeAll
    public static void Init(){
        faker = new Faker();
    }

    @MethodSource
    @ParameterizedTest
    public void greetingShouldReturnObjectFromService(GreetingModel model) {

        when(service.getGreetingMessage(model.content))
                .thenReturn(model);
        var controller = new GreetingController(service);
        var result = controller.greeting(model.content);

        assertThat(result == model).isTrue();
    }

    private static List<GreetingModel> greetingShouldReturnObjectFromService() {
        return List.of(
                new GreetingModel(faker.number().numberBetween(0,1000), faker.name().firstName()),
                new GreetingModel(0, faker.name().firstName()),
                new GreetingModel(faker.number().numberBetween(1,1000), "")
        );
    }
}
