package com.project.mygym.resource;

import com.project.mygym.Fixture;
import com.project.mygym.IntegrationBase;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.representation.ExerciseProgressRepresentation;
import com.project.mygym.representation.ExerciseRepresentation;
import com.project.mygym.representation.ProgramRepresentation;
import com.project.mygym.representation.SimpleUserRepresentation;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimpleUserResourceTest extends IntegrationBase {

    @Inject
    SimpleUserRepository simpleUserRepository;
    @Inject
    ProgramRepository programRepository;

    @Test
    @TestTransaction
    public void getTailoredProgramsTest() {
        String uri = Fixture.API_ROOT + GymURI.SIMPLE_USERS + "/1000/programs";
        System.out.println(uri);
        List<ProgramRepresentation> programs =
                given().queryParam("userId", 1000L)
                .when()
                .get(uri)
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {
                });
        SimpleUser simpleUser = simpleUserRepository.findById(1000L);
        assertEquals(programs.size(), 2);
        for (ProgramRepresentation programRepresentation: programs)
            assertTrue(programRepresentation.aimedAt.contains(simpleUser.getPhysicalCondition().label.toUpperCase()));
    }

    @Test
    @TestTransaction
    public void alreadySubscribedTest() {
        when().post(Fixture.API_ROOT + GymURI.SIMPLE_USERS + "/1000/subscribe/3000")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        SimpleUser simpleUser = simpleUserRepository.findById(1000L);
        Program program = programRepository.findById(3000L);
        assertEquals(simpleUser.getSubscriptions().size(), 1);
    }

    @Test
    @TestTransaction
    public void unsubscribeTest() {
        when().post(Fixture.API_ROOT + GymURI.SIMPLE_USERS + "/1000/unsubscribe/3000")
                .then()
                .statusCode(204);

        SimpleUser simpleUser = simpleUserRepository.findById(1000L);
        assertEquals(simpleUser.getSubscriptions().size(), 0);
    }

    @Test
    @TestTransaction
    public void startExerciseTest() {
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.SIMPLE_USERS + "/" + Fixture.getSimpleUserRepresentation1().id + "/exercise/" + Fixture.getExerciseRepresentation1().id).build();
        System.out.println(uri.toString());
        when().post(uri)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        SimpleUser simpleUser = simpleUserRepository.findById(1000L);
        assertEquals(simpleUser.getExerciseProgresses().size(), 3);
    }

    @Test
    @TestTransaction
    public void updateExerciseTest() {
        ExerciseProgressRepresentation exerciseProgressRepresentation = Fixture.getExerciseProgressRepresentation();
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.SIMPLE_USERS + "/exercise").build();
        given().contentType(ContentType.JSON)
                .body(exerciseProgressRepresentation)
                .when().put(uri)
                .then()
                .statusCode(200);
        SimpleUser simpleUser = simpleUserRepository.findById(1000L);
        assertEquals(simpleUser.getExerciseProgresses().size(), 2);
    }

}