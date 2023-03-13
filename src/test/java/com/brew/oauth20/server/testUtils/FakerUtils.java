package com.brew.oauth20.server.testUtils;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.github.javafaker.Faker;

import java.util.UUID;

public class FakerUtils {
    public static UUID createRandomUUID(Faker faker) {
        return UUID.fromString(faker.internet().uuid().replaceAll("_", ""));
    }

    public static ResponseType createRandomResponseType(Faker faker) {
        String[] responseTypeOptions = {"code", "token"};
        var responseTypeString = faker.options().option(responseTypeOptions);
        return ResponseType.valueOf(responseTypeString);
    }

    public static String createRandomRedirectUri(Faker faker) {
        return faker.internet().url();
    }


}
