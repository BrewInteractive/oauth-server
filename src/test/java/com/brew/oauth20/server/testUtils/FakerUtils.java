package com.brew.oauth20.server.testUtils;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.github.javafaker.Faker;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FakerUtils {

    private static final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{
            ResponseType.CODE,
            ResponseType.TOKEN};

    public static @NotNull UUID createRandomUUID(Faker faker) {
        return UUID.fromString(faker.internet().uuid().replaceAll("_", ""));
    }

    public static ResponseType createRandomResponseType(Faker faker) {
        return createRandomResponseType(faker, defaultResponseTypeOptions);
    }

    public static ResponseType createRandomResponseType(@NotNull Faker faker, String[] responseTypeOptions) {
        var responseTypeString = faker.options().option(responseTypeOptions);
        return ResponseType.valueOf(responseTypeString);
    }

    public static ResponseType createRandomResponseType(@NotNull Faker faker, ResponseType[] responseTypeOptions) {
        return faker.options().option(responseTypeOptions);

    }

    public static String createRandomRedirectUri(@NotNull Faker faker) {
        return faker.internet().url();
    }
}
