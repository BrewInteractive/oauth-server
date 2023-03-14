package com.brew.oauth20.server.service;

import com.brew.oauth20.server.exception.UnsupportedResponseTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.management.ServiceNotFoundException;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class ServiceFactory<TEnum, TType> {
    private Map<TEnum, Type> RegisteredServiceTypes;

    @Autowired
    private ApplicationContext context;


    public TType getService(TEnum providerType) throws ServiceNotFoundException, UnsupportedResponseTypeException {

        Type type = getRegisteredServiceTypes().get(providerType);
        Class<?> classType = (Class<?>) type;

        if (classType == null)
            throw new UnsupportedResponseTypeException();

        var service = (TType) context.getBean(classType);

        if (service == null)
            throw new ServiceNotFoundException();

        return service;
    }

    public Map<TEnum, Type> getRegisteredServiceTypes() {
        return RegisteredServiceTypes;
    }

    public void setRegisteredServiceTypes(Map<TEnum, Type> registeredServiceTypes) {
        RegisteredServiceTypes = registeredServiceTypes;
    }
}