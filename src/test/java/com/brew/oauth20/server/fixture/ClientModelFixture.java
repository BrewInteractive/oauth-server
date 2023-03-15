package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.ClientModel;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class ClientModelFixture extends Fixture<ClientModel> {

    private final Integer defaultGrantSize = 2;
    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.CODE, ResponseType.TOKEN};
    private final Integer defaultRedirectUriSize = 2;

    private final GrantModelFixture grantModelFixture;
    private final RedirectUriModelFixture redirectUriModelFixture;

    public ClientModelFixture() {
        this.grantModelFixture = new GrantModelFixture();
        this.redirectUriModelFixture = new RedirectUriModelFixture();
    }

    public ClientModel createRandomOne() {
        return createRandomOne(this.defaultGrantSize, this.defaultResponseTypeOptions);
    }

    public ClientModel createRandomOne(Integer grantSize, ResponseType[] responseTypeOptions) {
        return Instancio.of(clientModel(grantSize, responseTypeOptions))
                .create();
    }

    public List<ClientModel> createRandomList(Integer size) {
        return createRandomList(size, this.defaultGrantSize, this.defaultResponseTypeOptions);
    }

    public List<ClientModel> createRandomList(Integer size, Integer grantSize, ResponseType[] responseTypeOptions) {
        return Instancio.ofList(clientModel(grantSize, responseTypeOptions))
                .size(size)
                .create();
    }

    private Model<ClientModel> clientModel(Integer grantSize, ResponseType[] responseTypeOptions) {
        return Instancio.of(ClientModel.class)
                .supply(field(ClientModel::grantList), () -> grantModelFixture.createRandomList(grantSize, responseTypeOptions))
                .supply(field(ClientModel::redirectUriList), () -> redirectUriModelFixture.createRandomList(this.defaultRedirectUriSize))
                .toModel();
    }
}


