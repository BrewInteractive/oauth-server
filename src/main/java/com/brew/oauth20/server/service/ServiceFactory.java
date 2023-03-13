package com.brew.oauth20.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class ServiceFactory<TEnum, TType> {
    private Map<TEnum, Type> RegisteredServiceTypes;

    @Autowired
    private ApplicationContext context;


    public TType getService(TEnum providerType) {

        Type classType = getRegisteredServiceTypes().get(providerType);
        Class<?> clazz = (Class<?>) classType;

        return (TType) context.getBean(clazz);
    }

    public Map<TEnum, Type> getRegisteredServiceTypes() {
        return RegisteredServiceTypes;
    }

    public void setRegisteredServiceTypes(Map<TEnum, Type> registeredServiceTypes) {
        RegisteredServiceTypes = registeredServiceTypes;
    }
}