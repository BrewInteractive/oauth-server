package com.brew.oauth20.server.service.factory;

import com.brew.oauth20.server.exception.MissingServiceException;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class ServiceFactory<E extends Enum<E>, T> {
    private Map<E, Type> registeredServiceTypes;

    @Autowired
    private ApplicationContext context;

    public T getService(E providerType) throws MissingServiceException, UnsupportedServiceTypeException {

        Type type = getRegisteredServiceTypes().get(providerType);
        Class<?> classType = (Class<?>) type;

        if (classType == null)
            throw new UnsupportedServiceTypeException(providerType.toString());

        try {
            return (T) context.getBean(classType);
        } catch (BeansException e) {
            throw new MissingServiceException(e);
        }
    }

    public Map<E, Type> getRegisteredServiceTypes() {
        return registeredServiceTypes;
    }

    public void setRegisteredServiceTypes(Map<E, Type> registeredServiceTypes) {
        this.registeredServiceTypes = registeredServiceTypes;
    }
}
