package com.brew.oauth20.server.utils;

import com.brew.oauth20.server.model.ValidationResultModel;

public interface Validator<T> {
    ValidationResultModel validate(T entity);
}
