package com.brew.oauth20.server.manager;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCookieManagerTest {
    private Faker faker;

    @BeforeAll
    void setup() {
        this.faker = new Faker();
    }

    @Test
    void should_get_user_id_if_valid_cookie_exists() {
        // Arrange
        var userId = faker.random().nextLong();

        // Act
        var actualUserId = UserCookieManager.getUser();

        // Assert
        assertThat(actualUserId)
                .isNotNull()
                .contains(userId);
       
    }

    @Test
    void should_get_null_value_if_cookie_does_not_exist() {
        // Act
        var actualUserId = UserCookieManager.getUser();

        // Assert
        assertThat(actualUserId).isNull();
    }

    @Test
    void should_get_null_value_if_cookie_is_expired() {
        // Act
        var actualUserId = UserCookieManager.getUser();

        // Assert
        assertThat(actualUserId).isNull();
    }
}