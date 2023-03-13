package com.brew.oauth20.server.utils.interfaces;

import com.brew.oauth20.server.model.ValidationResultModel;

public interface Validator<T> {
    ValidationResultModel validate(T model);
}
