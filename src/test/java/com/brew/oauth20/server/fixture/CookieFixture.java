package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import jakarta.servlet.http.Cookie;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;


public class CookieFixture extends Fixture<Cookie> {

    public CookieFixture() {
        super();
    }

    public Cookie createDefaultOne() {
        var cookie = Instancio.of(cookieModel())
                .create();

        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);

        return cookie;
    }

    public Cookie createRandomOne() {
        return Instancio.of(cookieModel())
                .create();
    }

    public Set<Cookie> createRandomList(Integer size) {
        return Instancio.ofSet(cookieModel())
                .size(size)
                .create();
    }

    private Model<Cookie> cookieModel() {
        return Instancio.of(Cookie.class)
                .supply(field(Cookie::getName), () -> faker.lorem().characters())
                .supply(field(Cookie::getValue), () -> faker.lorem().characters())
                .toModel();
    }
}