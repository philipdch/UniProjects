package com.project.mygym.representation;

import com.project.mygym.Fixture;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.values.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimpleUserMapperTest {

    @Inject
    SimpleUserMapper mapper;

    @Test
    void toRepresentation() {
        SimpleUser simpleUser = new SimpleUser("username", "phoneNumber", "email", Gender.M, 180, 70D, LocalDate.now(), PhysicalCondition.NORMAL);
        simpleUser.setId(3000L);
        simpleUser.setPassword("12345".getBytes());

        SimpleUserRepresentation simpleUserRepresentation = mapper.toRepresentation(simpleUser);

        mappingAssertions(simpleUser, simpleUserRepresentation);
    }

    @Test
    void toModel() {
        SimpleUserRepresentation simpleUserRepresentation = Fixture.getSimpleUserRepresentation2();
        SimpleUser simpleUser = mapper.toModel(simpleUserRepresentation);

        mappingAssertions(simpleUser, simpleUserRepresentation);
    }

    private void mappingAssertions(SimpleUser simpleUser, SimpleUserRepresentation simpleUserRepresentation){
        assertEquals(simpleUser.getId(), simpleUserRepresentation.id);
        assertEquals(simpleUser.getUsername(), simpleUserRepresentation.username);
        assertEquals(simpleUser.getPhoneNumber(), simpleUserRepresentation.phoneNumber);
        assertEquals(simpleUser.getEmail(), simpleUserRepresentation.email);
        assertEquals(simpleUser.getGender(), simpleUserRepresentation.gender);
        assertEquals(simpleUser.getHeight(), simpleUserRepresentation.height);
        assertEquals(simpleUser.getWeight(), simpleUserRepresentation.weight);
        assertEquals(simpleUser.getDateOfBirth(), LocalDate.parse(simpleUserRepresentation.dateOfBirth));
        assertEquals(simpleUser.getPhysicalCondition().label, simpleUserRepresentation.physicalCondition);
    }
}
