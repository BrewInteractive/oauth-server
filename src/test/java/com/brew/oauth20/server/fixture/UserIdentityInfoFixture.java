package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;

import java.util.Map;

public class UserIdentityInfoFixture extends Fixture<Map<String, Object>> {

    public UserIdentityInfoFixture() {
        super();
    }

    public Map<String, Object> createRandomOne() {
        return userIdentityInfo();
    }

    private Map<String, Object> userIdentityInfo() {
        Map<String, Object> detail = new java.util.HashMap<>();
        for (int i = 0; i < faker.random().nextInt(1, 5); i++) {
            detail.put(faker.lordOfTheRings().location(), faker.lordOfTheRings().character());
        }
        return detail;
    }
}
