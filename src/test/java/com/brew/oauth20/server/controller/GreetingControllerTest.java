package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.model.GreetingModel;
import com.brew.oauth20.server.service.GreetingService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GreetingControllerTest {
    private static Faker faker;
    @MockBean
    private GreetingService greetingService;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    private static List<GreetingModel> greeting_should_return_object_from_service() {
        return List.of(
                new GreetingModel(faker.number().numberBetween(0, 1000), faker.name().firstName()),
                new GreetingModel(0, faker.name().firstName()),
                new GreetingModel(faker.number().numberBetween(1, 1000), "")
        );
    }

    @MethodSource
    @ParameterizedTest
    void greeting_should_return_object_from_service(GreetingModel model) {
        Mockito.reset(greetingService);
        when(greetingService.getGreetingModel(model.content()))
                .thenReturn(model);
        var controller = new GreetingController(greetingService);
        var result = controller.greeting(model.content());

        assertSame(result, model);
    }
}
