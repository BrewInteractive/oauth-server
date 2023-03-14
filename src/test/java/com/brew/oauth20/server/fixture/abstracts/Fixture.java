package com.brew.oauth20.server.fixture.abstracts;

import com.github.javafaker.Faker;

public abstract class Fixture<T> {

    protected Faker faker;

    protected Fixture() {
        this.faker = new Faker();
    }
}
