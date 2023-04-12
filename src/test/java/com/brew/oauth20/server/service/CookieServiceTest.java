package com.brew.oauth20.server.service;

import com.brew.oauth20.server.fixture.CookieFixture;
import com.brew.oauth20.server.service.impl.UserCookieServiceImpl;
import com.github.javafaker.Faker;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CookieServiceTest {

    private static Faker faker;
    CookieFixture cookieFixture;

    @BeforeAll
    public static void init() {
        faker = new Faker();
    }

    @Test
    void should_remove_user_cookie_by_key() {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie cookie = cookieFixture.createRandomOne();
        var expected = new Cookie(cookie.getName(), null);
        expected.setMaxAge(0);
        expected.setSecure(true);
        expected.setHttpOnly(true);
        expected.setPath("/");
        response.addCookie(cookie);

        // Assert
        var service = new UserCookieServiceImpl();
        service.deleteUserCookie(response, cookie.getName());

        assertThat(response.getCookies()).contains(expected);
    }

    @Test
    void should_get_user_cookie_by_key() {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = cookieFixture.createRandomOne();
        request.setCookies(cookie);

        // Assert
        var service = new UserCookieServiceImpl();
        var result = service.getUserCookie(request, cookie.getName());

        assertThat(result).isEqualTo(cookie.getValue());
    }


    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void should_not_get_user_cookie_by_key(boolean requestHasCookie) {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (requestHasCookie)
            request.setCookies(new Cookie("existingkey", "test"));

        // Act
        var service = new UserCookieServiceImpl();
        var result = service.getUserCookie(request, "notexistingkey");

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void should_set_cookie_with_key_value_fields() {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie cookie = cookieFixture.createDefaultOne();

        // Act
        var service = new UserCookieServiceImpl();
        service.setUserCookie(response, cookie.getName(), cookie.getValue());
        var result = response.getCookie(cookie.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void should_set_cookie_with_key_value_max_age_fields() {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletResponse response = new MockHttpServletResponse();
        var maxAge = faker.random().nextInt(100);
        Cookie cookie = cookieFixture.createDefaultOne();
        cookie.setMaxAge(maxAge * 60);

        // Act
        var service = new UserCookieServiceImpl();
        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge);
        var result = response.getCookie(cookie.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void should_set_cookie_with_key_value_max_age_http_only_fields() {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletResponse response = new MockHttpServletResponse();
        var maxAge = faker.random().nextInt(100);
        var isHttpOnly = faker.random().nextBoolean();
        Cookie cookie = cookieFixture.createDefaultOne();
        cookie.setMaxAge(maxAge * 60);
        cookie.setHttpOnly(isHttpOnly);

        // Act
        var service = new UserCookieServiceImpl();
        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge, isHttpOnly);
        var result = response.getCookie(cookie.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void should_set_cookie_with_key_value_max_age_http_only_secure_fields() {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletResponse response = new MockHttpServletResponse();
        var maxAge = faker.random().nextInt(100);
        var isHttpOnly = faker.random().nextBoolean();
        var isSecure = faker.random().nextBoolean();
        Cookie cookie = cookieFixture.createDefaultOne();
        cookie.setMaxAge(maxAge * 60);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(isSecure);

        // Act
        var service = new UserCookieServiceImpl();
        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge, isHttpOnly, isSecure);
        var result = response.getCookie(cookie.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void should_set_cookie_with_key_value_max_age_http_only_secure_path_fields() {
        // Arrange
        cookieFixture = new CookieFixture();
        MockHttpServletResponse response = new MockHttpServletResponse();
        var maxAge = faker.random().nextInt(100);
        var isHttpOnly = faker.random().nextBoolean();
        var isSecure = faker.random().nextBoolean();
        var path = faker.internet().url();
        Cookie cookie = cookieFixture.createDefaultOne();
        cookie.setMaxAge(maxAge * 60);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(isSecure);
        cookie.setPath(path);

        // Act
        var service = new UserCookieServiceImpl();
        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge, isHttpOnly, isSecure, path);
        var result = response.getCookie(cookie.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }
}
