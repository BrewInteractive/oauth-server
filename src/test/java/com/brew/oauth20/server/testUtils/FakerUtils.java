package com.brew.oauth20.server.testUtils;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FakerUtils {
    public static UUID createRandomUUID(Faker faker) {
        return UUID.fromString(faker.internet().uuid().replaceAll("_", ""));
    }

    public static String createRandomResponseType(Faker faker) {
        String[] responseTypeOptions = {"code", "token"};
        return faker.options().option(responseTypeOptions);
    }

    public static String createRandomRedirectUri(Faker faker) {
        return faker.internet().url();
    }

    public static ArrayList<String> createRandomRedirectUriList(Faker faker) {
        return IntStream.range(0, 3)
                .mapToObj(i -> createRandomRedirectUri(faker))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
