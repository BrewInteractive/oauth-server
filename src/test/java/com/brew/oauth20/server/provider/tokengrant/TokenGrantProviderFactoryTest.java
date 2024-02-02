package com.brew.oauth20.server.provider.tokengrant;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.service.ClientService;
import com.brew.oauth20.server.service.RefreshTokenService;
import com.brew.oauth20.server.service.TokenService;
import com.brew.oauth20.server.service.UserIdentityService;
import com.brew.oauth20.server.service.factory.TokenGrantProviderFactory;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TokenGrantProviderFactoryTest {
    @Mock
    ClientService clientService;
    @Mock
    TokenService tokenService;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    UserIdentityService userIdentityService;
    @Autowired
    Environment env;
    @InjectMocks
    TokenGrantProviderFactory tokenGrantProviderFactory;
    @Mock
    private ApplicationContext context;

    @Test
    void should_return_token_grant_refresh_token_provider_object() {
        Mockito.reset(context);
        Class<?> classType = TokenGrantProviderRefreshToken.class;
        TokenGrantProviderRefreshToken mockTokenProvider = new TokenGrantProviderRefreshToken(clientService, tokenService, userIdentityService, env, refreshTokenService);
        when((BaseTokenGrantProvider) context.getBean(classType)).thenReturn(mockTokenProvider);
        when((BaseTokenGrantProvider) context.getBean(classType)).thenReturn(mockTokenProvider);

        var service = tokenGrantProviderFactory.getService(GrantType.refresh_token);
        assertThat(service).isInstanceOf(TokenGrantProviderRefreshToken.class);
    }
}

