package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.data.enums.Scope;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class ClientFixture extends Fixture<Client> {

    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.code,
            ResponseType.token};
    private final Scope[] defaultScopeOptions = new Scope[]{Scope.openid,
            Scope.profile, Scope.email};
    private final GrantType[] defaultGrantTypeOptions = new GrantType[]{GrantType.authorization_code,
            GrantType.client_credentials, GrantType.refresh_token};

    private final ClientGrantFixture clientGrantFixture;
    private final ClientScopeFixture clientScopeFixture;
    private final RedirectUriFixture redirectUriModelFixture;
    private final Integer defaultClientGrantSize = 1;
    private final Integer defaultClientScopeSize = 1;
    private final Integer defaultRedirectUriSize = 1;


    public ClientFixture() {
        super();
        this.clientGrantFixture = new ClientGrantFixture();
        this.clientScopeFixture = new ClientScopeFixture();
        this.redirectUriModelFixture = new RedirectUriFixture();
    }

    public Client createRandomOne(Boolean withChildren) {
        return createRandomOne(defaultResponseTypeOptions, defaultGrantTypeOptions, defaultScopeOptions, withChildren);
    }

    public Client createRandomOne(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions, Scope[] scopeOptions) {
        return createRandomOne(responseTypeOptions, grantTypeOptions, scopeOptions, true);
    }

    private Client createRandomOne(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions, Scope[] scopeOptions, Boolean withChildren) {
        return Instancio.of(client(responseTypeOptions, grantTypeOptions, scopeOptions, withChildren))
                .create();
    }

    private Model<Client> client(ResponseType[] responseTypeOptions, GrantType[] grantTypeOptions, Scope[] scopeOptions, Boolean withChildren) {
        var model = Instancio.of(Client.class)
                .supply(field(Client::getName), () -> faker.name().title())
                .supply(field(Client::getClientSecret), () -> encodeClientSecret(faker.letterify("?".repeat(64))))
                .supply(field(Client::getId), UUID::randomUUID)
                .supply(field(Client::getCreatedAt),
                        () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(Client::getUpdatedAt),
                        () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(Client::getClientId), () -> faker.letterify("?????????"));

        if (withChildren) {
            model = model
                    .supply(field(Client::getClientGrants),
                            () -> clientGrantFixture.createRandomList(this.defaultClientGrantSize, responseTypeOptions, grantTypeOptions))
                    .supply(field(Client::getRedirectUris),
                            () -> redirectUriModelFixture.createRandomList(this.defaultRedirectUriSize))
                    .supply(field(Client::getClientScopes),
                            () -> clientScopeFixture.createRandomList(this.defaultClientScopeSize, scopeOptions));
        } else {
            model = model
                    .supply(field(Client::getClientGrants), () -> new LinkedHashSet<>())
                    .supply(field(Client::getRedirectUris), () -> new LinkedHashSet<>())
                    .supply(field(Client::getClientUsers), () -> new LinkedHashSet<>())
                    .supply(field(Client::getClientScopes), () -> new LinkedHashSet<>());
        }

        return model.toModel();
    }

    private String encodeClientSecret(String clientSecret) {
        byte[] encodedBytes = Base64.getEncoder().encode(clientSecret.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }
}