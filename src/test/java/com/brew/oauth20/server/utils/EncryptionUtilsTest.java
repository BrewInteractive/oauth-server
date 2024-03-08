package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.testUtils.FakerUtils;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EncryptionUtilsTest {
    private Faker faker;

    @BeforeAll
    void Setup() {
        this.faker = new Faker();
    }

    @SneakyThrows
    @Test
    void should_encrypt_and_decrypt_data() {
        // Arrange
        var secret = faker.regexify("[A-Za-z0-9]{16}");
        var algorithm = "AES";
        var cipherSpec = "AES/GCM/NoPadding";
        var algorithms = EncryptionUtils.createAlgorithmKeyHashmap(algorithm, cipherSpec);

        // Act
        var testData = faker.lordOfTheRings().location();
        var encryptedData = EncryptionUtils.encrypt(testData, algorithms, secret);
        var decryptedData = EncryptionUtils.decrypt(encryptedData, algorithms, secret);

        // Assert
        Assertions.assertEquals(decryptedData, testData);
    }

    @Test
    void should_not_decrypt_null_data() {
        // Arrange
        var secret = faker.regexify("[A-Za-z0-9]{16}");
        var algorithm = "AES";
        var cipherSpec = "AES/GCM/NoPadding";
        var algorithms = EncryptionUtils.createAlgorithmKeyHashmap(algorithm, cipherSpec);

        // Assert
        assertThrows(Exception.class, () -> EncryptionUtils.decrypt(null, algorithms, secret));
    }

    @Test
    void should_not_decrypt_invalid_data() {
        // Arrange
        var secret = FakerUtils.create128BitRandomString(faker);
        var algorithm = "AES";
        var cipherSpec = "AES/GCM/NoPadding";
        var algorithms = EncryptionUtils.createAlgorithmKeyHashmap(algorithm, cipherSpec);

        // Assert
        assertThrows(Exception.class, () -> EncryptionUtils.decrypt("invalid-encrypted-data", algorithms, secret));
    }
}
