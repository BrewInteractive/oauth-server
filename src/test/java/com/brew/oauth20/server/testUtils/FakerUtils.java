package com.brew.oauth20.server.testUtils;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.data.enums.Scope;
import com.github.javafaker.Faker;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FakerUtils {

    private static final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{
            ResponseType.code,
            ResponseType.token};

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

    public static GrantType createRandomGrantType(@NotNull Faker faker, GrantType[] grantTypeOptions) {
        return faker.options().option(grantTypeOptions);
    }

    public static Scope createRandomScope(@NotNull Faker faker, Scope[] scopeOptions) {
        return faker.options().option(scopeOptions);
    }

    public static Set<Scope> createRandomScopeList(@NotNull Faker faker, Scope[] scopeOptions) {
        var totalScopeCount = scopeOptions.length;
        var numberOfScopesToCreate = faker.random().nextInt(1, totalScopeCount);
        var createdScopes = new HashSet<Scope>();
        var availableScopeOptions = new ArrayList<>(Arrays.asList(scopeOptions));

        for (int i = 0; i < numberOfScopesToCreate; i++) {
            // Update available scopes (filter out the already created ones)
            availableScopeOptions.removeAll(createdScopes);
            createdScopes.add(createRandomScope(faker, availableScopeOptions.toArray(new Scope[0])));
        }
        return createdScopes;
    }

    public static String createRandomRedirectUri(@NotNull Faker faker) {
        return faker.internet().url();
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
