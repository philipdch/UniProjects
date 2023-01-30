package com.project.mygym.resource;

import com.project.mygym.Fixture;
import com.project.mygym.IntegrationBase;
import com.project.mygym.representation.ExerciseRepresentation;
import com.project.mygym.representation.ProgramRepresentation;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ProgramResourceTest extends IntegrationBase {

    @Test
    @TestTransaction
    public void getAllPrograms() {
        List<ProgramRepresentation> programs = when().get(Fixture.API_ROOT + GymURI.PROGRAMS)
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {
                });
        assertEquals(programs.size(), 2);
        assertEquals(Fixture.getProgramRepresentation().id, programs.get(0).id);
    }

    @Test
    @TestTransaction
    public void getProgramByName() {
        List<ProgramRepresentation> programs = given().queryParam("name", "First program").when().get(Fixture.API_ROOT + GymURI.PROGRAMS)
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {
                });
        assertEquals(programs.size(), 1);
        assertEquals(Fixture.getProgramRepresentation().id, programs.get(0).id);
    }

    @Test
    @TestTransaction
    public void getProgramByAge() {
        List<ProgramRepresentation> programs = given().queryParam("age", 90).when().get(Fixture.API_ROOT + GymURI.PROGRAMS)
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {
                });
        assertEquals(programs.size(), 1);
        assertEquals(Fixture.getProgramRepresentation().id, programs.get(0).id);
    }

    @Test
    @TestTransaction
    public void getNonexistentProgram() {
        when().get(Fixture.API_ROOT + GymURI.PROGRAMS + "/0")
                .then()
                .statusCode(404);
    }

    @Test
    @TestTransaction
    public void testProgramIntersection() {
        List<ProgramRepresentation> programs = given().queryParam("name", "First program").queryParam("age", 14)
                .when().get(Fixture.API_ROOT + GymURI.PROGRAMS)
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {
                });
        assertEquals(programs.size(), 0);
    }

    @Test
    @TestTransaction
    public void getProgramById() {
        ProgramRepresentation program = when().get(Fixture.API_ROOT + GymURI.PROGRAMS + "/3000")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {
                });
        assertEquals(program.id, 3000);
    }

    @Test
    @TestTransaction
    public void addExerciseToProgram() {
        ExerciseRepresentation exerciseRepresentation = Fixture.getExerciseRepresentation1();
        exerciseRepresentation.id = null;
        exerciseRepresentation.programId = 2000L;
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.PROGRAMS + "/" + Fixture.getProgramRepresentation().id + "/exercises").build();
        ExerciseRepresentation created = given().contentType(ContentType.JSON)
                .body(exerciseRepresentation)
                .when().post(uri)
                .then()
                .statusCode(201)
                .extract().as(ExerciseRepresentation.class);
        assertEquals(created.programId, Fixture.getProgramRepresentation().id);
        assertNotNull(created.id);
    }

    @Test
    @TestTransaction
    public void addExerciseToNonexistentProgram() {
        ExerciseRepresentation exerciseRepresentation = Fixture.getExerciseRepresentation1();
        exerciseRepresentation.id = null;
        exerciseRepresentation.programId = null;
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.PROGRAMS + "/0"  + "/exercises").build();
        given().contentType(ContentType.JSON)
                .body(exerciseRepresentation)
                .when().post(uri)
                .then()
                .statusCode(404);
    }
}