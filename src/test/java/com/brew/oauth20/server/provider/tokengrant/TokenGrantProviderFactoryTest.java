package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenGrantProviderFactoryTest {

    @Autowired
    TokenGrantProviderFactory tokenGrantProviderFactory;

    @Test
    void should_return_token_grant_refresh_token_provider_object() {
        var service = tokenGrantProviderFactory.getService(GrantType.refresh_token);
        assertThat(service).isInstanceOf(TokenGrantProviderRefreshToken.class);
    }
}

