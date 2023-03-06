package com.brew.oauth20.server.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.brew.oauth20.server.utils.StringUtils.GenerateSecureRandomString;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StringUtilsTest {
    @Test
    public void generateSecureRandomStringShouldReturnDifferentResults() {


        String  randomString1 = GenerateSecureRandomString();
        String  randomString2 = GenerateSecureRandomString();

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