package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.ClientModel;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class ClientModelFixture extends Fixture<ClientModel> {
    private final GrantModelFixture grantModelFixture;
    private final RedirectUriModelFixture redirectUriModelFixture;

    public ClientModelFixture() {
        this.grantModelFixture = new GrantModelFixture();
        this.redirectUriModelFixture = new RedirectUriModelFixture();
    }

    @Override
    public ClientModel createRandomOne() {
        return Instancio.of(validModel())
                .create();
    }

    @Override
    public List<ClientModel> createRandomList(Integer size) {
        return Instancio.ofList(validModel())
                .size(size)
                .create();
    }

    private Model<ClientModel> validModel() {
        return Instancio.of(ClientModel.class)
                .set(field(ClientModel::grantList), grantModelFixture.createRandomList(2))
                .set(field(ClientModel::redirectUriList), redirectUriModelFixture.createRandomList(2))
                .toModel();
    }
}
