package com.brew.oauth20.server.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
    @Test
    void generate_secure_random_string_should_return_different_results() {
        String randomString1 = StringUtils.generateSecureRandomString();
        String randomString2 = StringUtils.generateSecureRandomString();

        assertNotEquals("", randomString1);
        assertNotEquals("", randomString2);

        assertEquals(32, randomString1.length());
        assertEquals(32, randomString2.length());

        var regexSchema = "[0-9a-zA-Z-_]+";

        assertTrue(randomString1.matches(regexSchema));
        assertTrue(randomString2.matches(regexSchema));

        assertNotEquals(randomString1, randomString2);
    }
}