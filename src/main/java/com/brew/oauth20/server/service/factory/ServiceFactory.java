package com.brew.oauth20.server.service.factory;

import com.brew.oauth20.server.exception.MissingServiceException;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class ServiceFactory<TEnum, TType> {
    private Map<TEnum, Type> RegisteredServiceTypes;

    @Autowired
    private ApplicationContext context;


    public TType getService(TEnum providerType) throws MissingServiceException, UnsupportedServiceTypeException {

        Type type = getRegisteredServiceTypes().get(providerType);
        Class<?> classType = (Class<?>) type;

        if (classType == null)
            throw new UnsupportedServiceTypeException(providerType.toString());

        try {
            return (TType) context.getBean(classType);
        } catch (BeansException e) {
            throw new MissingServiceException(e);
        }
    }

    public Map<TEnum, Type> getRegisteredServiceTypes() {
        return RegisteredServiceTypes;
    }

    public void setRegisteredServiceTypes(Map<TEnum, Type> registeredServiceTypes) {
        RegisteredServiceTypes = registeredServiceTypes;
    }
}
