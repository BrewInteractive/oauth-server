package com.brew.oauth20.server.provider;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.exception.MissingServiceException;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderAuthorizationCode;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.provider.authorizetype.AuthorizeTypeProviderToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AuthorizeTypeProviderFactoryTest {

    @Autowired
    AuthorizeTypeProviderFactory authorizeTypeProviderFactory;

    @Test
    void factoryShouldReturnAuthorizeTypeCodeProviderObject() {
        var service = authorizeTypeProviderFactory.getService(ResponseType.code);
        assertThat(service).isInstanceOf(AuthorizeTypeProviderAuthorizationCode.class);
    }

    @Test
    void factoryShouldReturnAuthorizeTypeTokenProviderObject() {
        authorizeTypeProviderFactory.setRegisteredServiceTypes(
                Map.of(
                        ResponseType.code, AuthorizeTypeProviderAuthorizationCode.class,
                        ResponseType.token, AuthorizeTypeProviderToken.class
                )
        );
        var service = authorizeTypeProviderFactory.getService(ResponseType.token);
        assertThat(service).isInstanceOf(AuthorizeTypeProviderToken.class);
    }

    @Test
    void factoryShouldThrowsUnsupportedServiceTypeException() {
        authorizeTypeProviderFactory.setRegisteredServiceTypes(
                Map.of(
                        ResponseType.code, AuthorizeTypeProviderAuthorizationCode.class
                )
        );
        Exception exception = assertThrows(UnsupportedServiceTypeException.class, () -> authorizeTypeProviderFactory.getService(ResponseType.token));
        assertThat(exception.getMessage()).isEqualTo(ResponseType.token.toString());
    }


    @Test
    void factoryShouldThrowsMissingServiceException() {
        authorizeTypeProviderFactory.setRegisteredServiceTypes(
                Map.of(
                        ResponseType.code, MockAuthorizeTypeProviderNotExistingInIoc.class
                )
        );
        Exception exception = assertThrows(MissingServiceException.class, () -> authorizeTypeProviderFactory.getService(ResponseType.code));
        assertInstanceOf(BeansException.class, exception.getCause());
    }
}

