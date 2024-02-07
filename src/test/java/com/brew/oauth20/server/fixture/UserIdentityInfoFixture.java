package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class UserIdentityInfoFixture extends Fixture<Map<String, Object>> {

    public UserIdentityInfoFixture() {
        super();
    }

    public Map<String, Object> createRandomOne() {
        return userIdentityInfo();
    }

    public ResponseEntity<JsonNode> createRandomOneJsonResponse() {
        var userIdentityInfo = userIdentityInfo();
        var objectMapper = new ObjectMapper();
        var userIdentityInfoJson = objectMapper.convertValue(userIdentityInfo, JsonNode.class);
        return ResponseEntity.ok(userIdentityInfoJson);
    }

    private Map<String, Object> userIdentityInfo() {
        Map<String, Object> detail = new java.util.HashMap<>();
        for (int i = 0; i < faker.random().nextInt(2, 5); i++) {
            detail.put(faker.lordOfTheRings().location(), faker.lordOfTheRings().character());
        }
        return detail;
    }
}
