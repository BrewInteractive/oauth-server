package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.testUtils.FakerUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EncryptionUtilsTest {

    private static final String ALGORITHM = "AES";
    private Faker faker;

    @BeforeAll
    void Setup() {
        this.faker = new Faker();
    }

    @Test
    void should_encrypt_and_decrypt_properly() throws Exception {
        // Arrange
        var testData = faker.lordOfTheRings().location();
        var key = FakerUtils.create128BitRandomString(faker);

        // Act
        String encryptedData = EncryptionUtils.encrypt(testData, ALGORITHM, key);
        String decryptedData = EncryptionUtils.decrypt(encryptedData, ALGORITHM, key);

        // Assert
        assertThat(decryptedData).isEqualTo(testData);
    }

    @Test
    void should_not_encrypt_null_data() {
        // Arrange
        var key = FakerUtils.create128BitRandomString(faker);

        // Assert
        assertThrows(Exception.class, () -> {
            EncryptionUtils.encrypt(null, ALGORITHM, key);
        });
    }

    @Test
    void should_not_decrypt_null_data() {
        // Arrange
        var key = faker.regexify("[A-Za-z0-9]{16}");

        // Assert
        assertThrows(Exception.class, () -> {
            EncryptionUtils.decrypt(null, ALGORITHM, key);
        });
    }

    @Test
    void should_not_decrypt_invalid_data() {
        // Arrange
        var key = FakerUtils.create128BitRandomString(faker);

        // Assert
        assertThrows(Exception.class, () -> {
            EncryptionUtils.decrypt("invalid-encrypted-data", ALGORITHM, key);
        });
    }
}
