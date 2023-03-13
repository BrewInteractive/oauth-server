package com.brew.oauth20.server.fixture.abstracts;

import com.github.javafaker.Faker;

import java.util.List;

public abstract class Fixture<T> {

    protected Faker faker;

    protected Fixture() {
        this.faker = new Faker();
    }

    public abstract T createRandomOne();

    public abstract List<T> createRandomList(Integer size);
}
