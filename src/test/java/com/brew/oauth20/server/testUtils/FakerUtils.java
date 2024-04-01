package com.brew.oauth20.server.testUtils;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.github.javafaker.Faker;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FakerUtils {
    public static ResponseType createRandomResponseType(@NotNull Faker faker, ResponseType[] responseTypeOptions) {
        return faker.options().option(responseTypeOptions);
    }

    public static GrantType createRandomGrantType(@NotNull Faker faker, GrantType[] grantTypeOptions) {
        return faker.options().option(grantTypeOptions);
    }

    public static <T> T createRandomEnum(@NotNull Faker faker, T[] enumOptions) {
        return faker.options().option(enumOptions);
    }

    public static <T> Set<T> createRandomEnumList(@NotNull Faker faker, T[] enumOptions) {
        var totalEnumCount = enumOptions.length;
        var numberOfEnumsToCreate = faker.random().nextInt(1, totalEnumCount);
        var createdEnums = new HashSet<T>();
        var availableEnumOptions = new ArrayList<>(List.of(enumOptions));

        for (int i = 0; i < numberOfEnumsToCreate; i++) {
            // Update available scopes (filter out the already created ones)
            availableEnumOptions.removeAll(createdEnums);
            createdEnums.add(createRandomEnum(faker, (T[]) availableEnumOptions.toArray()));
        }
        return createdEnums;
    }

    public static String createRandomUri(@NotNull Faker faker) {
        return "https://" + faker.internet().url();
    }

    public static String createRandomWebOrigin(@NotNull Faker faker) {
        String randomUrl = faker.internet().url();
        randomUrl = addProtocol(randomUrl);
        return randomUrl;
    }

    @NotNull
    private static String addProtocol(String randomUrl) {
        if (!randomUrl.startsWith("http://") && !randomUrl.startsWith("https://")) {
            randomUrl = "http://" + randomUrl;
        }
        return randomUrl;
    }

    public static String create128BitRandomString(@NotNull Faker faker) {
        return faker.regexify("[A-Za-z0-9]{16}");
    }
}
