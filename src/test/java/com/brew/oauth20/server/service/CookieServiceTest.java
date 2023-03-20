package com.brew.oauth20.server.service;

import com.brew.oauth20.server.fixture.CookieFixture;
import com.brew.oauth20.server.service.impl.UserCookieServiceImpl;
import com.github.javafaker.Faker;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class CookieServiceTest {

    private static Faker faker;
    CookieFixture cookieFixture;

    @BeforeAll
    public static void Init() {
        faker = new Faker();
    }

    @Test
    void shouldRemoveUserCookieByKey() {
        cookieFixture = new CookieFixture();

        MockHttpServletResponse response = new MockHttpServletResponse();

        Cookie cookie = cookieFixture.createRandomOne();

        var expected = new Cookie(cookie.getName(), null);
        expected.setMaxAge(0);
        expected.setSecure(true);
        expected.setHttpOnly(true);
        expected.setPath("/");

        response.addCookie(cookie);

        var service = new UserCookieServiceImpl();

        service.deleteUserCookie(response, cookie.getName());

        assertThat(response.getCookies()).contains(expected);
    }

    @Test
    void shouldGetUserCookieByKey() {
        cookieFixture = new CookieFixture();

        MockHttpServletRequest request = new MockHttpServletRequest();

        Cookie cookie = cookieFixture.createRandomOne();

        request.setCookies(cookie);

        var service = new UserCookieServiceImpl();

        var result = service.getUserCookie(request, cookie.getName());

        assertThat(result).isEqualTo(cookie.getValue());
    }


    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldNotGetUserCookieByKey(boolean requestHasCookie) {
        cookieFixture = new CookieFixture();

        MockHttpServletRequest request = new MockHttpServletRequest();

        if (requestHasCookie)
            request.setCookies(new Cookie("existingkey", "test"));

        var service = new UserCookieServiceImpl();

        var result = service.getUserCookie(request, "notexistingkey");

        assertThat(result).isNull();
    }

    @Test
    void shouldSetCookieWithKeyValueFields() {
        cookieFixture = new CookieFixture();

        MockHttpServletResponse response = new MockHttpServletResponse();

        Cookie cookie = cookieFixture.createDefaultOne();

        var service = new UserCookieServiceImpl();

        service.setUserCookie(response, cookie.getName(), cookie.getValue());

        var result = response.getCookie(cookie.getName());

        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void shouldSetCookieWithKeyValueMaxAgeFields() {
        cookieFixture = new CookieFixture();

        MockHttpServletResponse response = new MockHttpServletResponse();

        var maxAge = faker.random().nextInt(100);
        Cookie cookie = cookieFixture.createDefaultOne();
        cookie.setMaxAge(maxAge * 60);

        var service = new UserCookieServiceImpl();

        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge);

        var result = response.getCookie(cookie.getName());

        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void shouldSetCookieWithKeyValueMaxAgeHttpOnlyFields() {
        cookieFixture = new CookieFixture();

        MockHttpServletResponse response = new MockHttpServletResponse();

        var maxAge = faker.random().nextInt(100);
        var isHttpOnly = faker.random().nextBoolean();
        Cookie cookie = cookieFixture.createDefaultOne();
        cookie.setMaxAge(maxAge * 60);
        cookie.setHttpOnly(isHttpOnly);

        var service = new UserCookieServiceImpl();

        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge, isHttpOnly);

        var result = response.getCookie(cookie.getName());

        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void shouldSetCookieWithKeyValueMaxAgeHttpOnlySecureFields() {
        cookieFixture = new CookieFixture();

        MockHttpServletResponse response = new MockHttpServletResponse();

        var maxAge = faker.random().nextInt(100);
        var isHttpOnly = faker.random().nextBoolean();
        var isSecure = faker.random().nextBoolean();
        Cookie cookie = cookieFixture.createDefaultOne();
        cookie.setMaxAge(maxAge * 60);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(isSecure);

        var service = new UserCookieServiceImpl();

        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge, isHttpOnly, isSecure);

        var result = response.getCookie(cookie.getName());

        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }

    @Test
    void shouldSetCookieWithKeyValueMaxAgeHttpOnlySecurePathFields() {
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

        var service = new UserCookieServiceImpl();

        service.setUserCookie(response, cookie.getName(), cookie.getValue(), maxAge, isHttpOnly, isSecure, path);

        var result = response.getCookie(cookie.getName());

        assertThat(cookie.getAttributes()).containsAllEntriesOf(result.getAttributes());
    }
}
