package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class CustomClaimFixture extends Fixture<Map<String, Object>> {

    public CustomClaimFixture() {
        super();
    }

    public Map<String, Object> createRandomOne() {
        return customClaim();
    }

    public ResponseEntity<JsonNode> createRandomOneJsonResponse() {
        var customClaim = customClaim();
        var objectMapper = new ObjectMapper();
        var customClaimJson = objectMapper.convertValue(customClaim, JsonNode.class);
        return ResponseEntity.ok(customClaimJson);
    }

    private Map<String, Object> customClaim() {
        Map<String, Object> detail = new java.util.HashMap<>();
        for (int i = 0; i < faker.random().nextInt(2, 5); i++) {
            detail.put(faker.harryPotter().house(), faker.harryPotter().character());
        }
        return detail;
    }
}
