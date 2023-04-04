package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class ClientFixture extends Fixture<Client> {

    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.code,
            ResponseType.token};
    private final ClientGrantFixture clientGrantFixture;
    private final RedirectUriFixture redirectUriModelFixture;
    private final Integer defaultClientGrantSize = 1;
    private final Integer defaultRedirectUriSize = 1;

    public ClientFixture() {
        super();
        this.clientGrantFixture = new ClientGrantFixture();
        this.redirectUriModelFixture = new RedirectUriFixture();
    }

    public Client createRandomOne(Boolean withChildren) {
        return createRandomOne(this.defaultResponseTypeOptions, withChildren);
    }

    public Client createRandomOne(ResponseType[] responseTypeOptions, Boolean withChildren) {
        return Instancio.of(client(responseTypeOptions, withChildren))
                .create();
    }

    public Set<Client> createRandomList(Integer size) {
        return createRandomList(size, this.defaultResponseTypeOptions);
    }

    public Set<Client> createRandomList(Integer size, ResponseType[] responseTypeOptions) {
        return Instancio.ofSet(client(responseTypeOptions, true))
                .size(size)
                .create();
    }

    private Model<Client> client(ResponseType[] responseTypeOptions, Boolean withChildren) {
        var model = Instancio.of(Client.class)
                .supply(field(Client::getName), () -> faker.name().title())
                .supply(field(Client::getId), () -> UUID.randomUUID())
                .supply(field(Client::getCreatedAt),
                        () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(Client::getUpdatedAt),
                        () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(Client::getClientId), () -> faker.letterify("?????????"));

        if (withChildren) {
            model = model
                    .supply(field(Client::getClientGrants),
                            () -> clientGrantFixture.createRandomList(this.defaultClientGrantSize,
                                    responseTypeOptions))
                    .supply(field(Client::getRedirectUris),
                            () -> redirectUriModelFixture.createRandomList(this.defaultRedirectUriSize));
        } else {
            model = model
                    .supply(field(Client::getClientGrants), () -> new LinkedHashSet<>())
                    .supply(field(Client::getRedirectUris), () -> new LinkedHashSet<>())
                    .supply(field(Client::getClientsUsers), () -> new LinkedHashSet<>());
        }

        return model.toModel();
    }
}