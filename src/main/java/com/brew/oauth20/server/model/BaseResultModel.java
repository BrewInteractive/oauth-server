package com.brew.oauth20.server.model;

import lombok.Data;


@Data
public abstract class BaseResultModel<T> {

    protected final T result;
    protected final String error;

    protected BaseResultModel(T result, String error) {
        this.result = result;
        this.error = error;
    }
}
