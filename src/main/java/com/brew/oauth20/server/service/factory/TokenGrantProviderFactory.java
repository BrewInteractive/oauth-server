package com.brew.oauth20.server.service.factory;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.provider.tokengrant.BaseTokenGrantProvider;
import com.brew.oauth20.server.provider.tokengrant.TokenGrantProviderAuthorizationCode;
import com.brew.oauth20.server.provider.tokengrant.TokenGrantProviderClientCredentials;
import com.brew.oauth20.server.provider.tokengrant.TokenGrantProviderRefreshToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class TokenGrantProviderFactory extends ServiceFactory<GrantType, BaseTokenGrantProvider> {
    public TokenGrantProviderFactory() {

        Map<GrantType, Type> map = Map.of(
                GrantType.refresh_token, TokenGrantProviderRefreshToken.class,
                GrantType.authorization_code, TokenGrantProviderAuthorizationCode.class,
                GrantType.client_credentials, TokenGrantProviderClientCredentials.class);

        setRegisteredServiceTypes(map);
    }
}