package com.brew.oauth20.server.fixture;

import com.brew.oauth20.server.fixture.abstracts.Fixture;
import com.brew.oauth20.server.model.UserIdentityInfoModel;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class UserIdentityInfoModelFixture extends Fixture<UserIdentityInfoModel> {

    public UserIdentityInfoModelFixture() {
        super();
    }

    public UserIdentityInfoModel createRandomOne() {
        return Instancio.of(userIdentityInfoModel())
                .create();
    }

    private Model<UserIdentityInfoModel> userIdentityInfoModel() {
        return Instancio.of(UserIdentityInfoModel.class)
                .supply(field(UserIdentityInfoModel::getSub), () -> faker.letterify("?".repeat(16)))
                .supply(field(UserIdentityInfoModel::getName_surname), () -> faker.name().fullName())
                .supply(field(UserIdentityInfoModel::getEmail), () -> faker.internet().emailAddress())
                .supply(field(UserIdentityInfoModel::getCreated_at), () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .supply(field(UserIdentityInfoModel::getUpdated_at), () -> faker.date().past(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .toModel();
    }
}
