package com.brew.oauth20.server.utils.abstracts;

public abstract class BaseValidator<T> {
    protected T model;

    protected BaseValidator(T model) {
        this.model = model;
    }
}
