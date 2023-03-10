package com.brew.oauth20.server.utils;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class StringUtilsTest {
    @Test
    void generateSecureRandomStringShouldReturnDifferentResults() {
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