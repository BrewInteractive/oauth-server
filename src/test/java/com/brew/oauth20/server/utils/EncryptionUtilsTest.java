package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.testUtils.FakerUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EncryptionUtilsTest {
    private Faker faker;

    @BeforeAll
    void Setup() {
        this.faker = new Faker();
    }

    @Test
    void should_encrypt_and_decrypt_via_AES_algorithm() throws Exception {
        // Arrange
        var testData = faker.lordOfTheRings().location();
        var key = FakerUtils.create128BitRandomString(faker);
        var algorithm = "AES";

        // Act
        String encryptedData = EncryptionUtils.encrypt(testData, algorithm, key);
        String decryptedData = EncryptionUtils.decrypt(encryptedData, algorithm, key);

        // Assert
        assertThat(decryptedData).isEqualTo(testData);
    }

    @Test
    void should_not_encrypt_null_data() {
        // Arrange
        var key = FakerUtils.create128BitRandomString(faker);
        var algorithm = "AES";

        // Assert
        assertThrows(Exception.class, () -> {
            EncryptionUtils.encrypt(null, algorithm, key);
        });
    }

    @Test
    void should_not_decrypt_null_data() {
        // Arrange
        var key = faker.regexify("[A-Za-z0-9]{16}");
        var algorithm = "AES";

        // Assert
        assertThrows(Exception.class, () -> {
            EncryptionUtils.decrypt(null, algorithm, key);
        });
    }

    @Test
    void should_not_decrypt_invalid_data() {
        // Arrange
        var key = FakerUtils.create128BitRandomString(faker);
        var algorithm = "AES";

        // Assert
        assertThrows(Exception.class, () -> {
            EncryptionUtils.decrypt("invalid-encrypted-data", algorithm, key);
        });
    }
}
