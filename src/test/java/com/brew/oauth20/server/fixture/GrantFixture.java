package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.data.Grant;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.testUtils.FakerUtils;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static org.instancio.Select.field;

public class GrantFixture extends Fixture<Grant> {
    private final ResponseType[] defaultResponseTypeOptions = new ResponseType[]{ResponseType.CODE, ResponseType.TOKEN};

    public Grant createRandomOne() {
        return createRandomOne(this.defaultResponseTypeOptions);
    }

    public Grant createRandomOne(ResponseType[] responseTypeOptions) {
        return Instancio.of(grant(responseTypeOptions))
                .create();
    }

    public Set<Grant> createRandomList(Integer size) {
        return createRandomList(size, this.defaultResponseTypeOptions);
    }

    public Set<Grant> createRandomList(Integer size, ResponseType[] responseTypeOptions) {
        return Instancio.ofSet(grant(responseTypeOptions))
                .size(size)
                .create();
    }

    private Model<Grant> grant(ResponseType[] responseTypeOptions) {
        return Instancio.of(Grant.class)
                .supply(field(Grant::getResponseType), () -> FakerUtils.createRandomResponseType(faker, responseTypeOptions))
                .toModel();
    }
}
