package com.brew.oauth20.server.model;

import lombok.Data;


@Data
public abstract class BaseResultModel<T> {

    public T result;
    public String error;

    protected BaseResultModel(T result, String error) {
        this.result = result;
        this.error = error;
    }
}
