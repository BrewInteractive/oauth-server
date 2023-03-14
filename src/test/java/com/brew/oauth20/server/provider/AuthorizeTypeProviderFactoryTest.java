package com.brew.oauth20.server.provider;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.provider.AuthorizeType.AuthorizeTypeProviderFactory;
import com.brew.oauth20.server.provider.AuthorizeType.Code.AuthorizeTypeProviderCode;
import com.brew.oauth20.server.provider.AuthorizeType.Token.AuthorizeTypeProviderToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthorizeTypeProviderFactoryTest {

    @Autowired
    AuthorizeTypeProviderFactory authorizeTypeProviderFactory;

    @Test
    void factoryShouldReturnAuthorizeTypeCodeProviderObject() {
        var service = authorizeTypeProviderFactory.getService(ResponseType.code);
        assertThat(service instanceof AuthorizeTypeProviderCode).isTrue();
    }

    @Test
    void factoryShouldReturnAuthorizeTypeTokenProviderObject() {
        var service = authorizeTypeProviderFactory.getService(ResponseType.token);
        assertThat(service instanceof AuthorizeTypeProviderToken).isTrue();
    }
}
