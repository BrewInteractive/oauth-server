package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.GreetingModel;
import com.brew.oauth20.server.service.impl.GreetingServiceImpl;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingServiceTest {

    private static Faker faker;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    private static Stream<Arguments> get_greeting_model_should_return_greeting_model() {

        var name1 = faker.name().firstName();
        var name2 = faker.name().firstName();

        return Stream.of(
                Arguments.of(
                        new GreetingModel(1, name1),
                        new GreetingModel(1, "Hello, " + name1 + "!")
                ),
                Arguments.of(
                        new GreetingModel(1, name2),
                        new GreetingModel(1, "Hello, " + name2 + "!")
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    void get_greeting_model_should_return_greeting_model(GreetingModel input, GreetingModel expected) {

        var service = new GreetingServiceImpl();
        var result = service.getGreetingModel(input.content());

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
