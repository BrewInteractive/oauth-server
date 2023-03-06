package com.brew.oauth20.server.Service;

import com.brew.oauth20.server.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TokenServiceTest {
    @Test
    public void generateRandomTokenStringShouldReturnDifferentResults() {

        var tokenService = new TokenServiceImpl();

        String  randomString1 = tokenService.generateRandomTokenString();
        String  randomString2 = tokenService.generateRandomTokenString();

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
