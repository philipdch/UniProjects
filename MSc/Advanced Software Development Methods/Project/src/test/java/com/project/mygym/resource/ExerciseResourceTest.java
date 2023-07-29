package com.project.mygym.resource;

import com.project.mygym.Fixture;
import com.project.mygym.IntegrationBase;
import com.project.mygym.representation.ExerciseRepresentation;
import com.project.mygym.representation.ProgramRepresentation;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ExerciseResourceTest extends IntegrationBase {

    @Test
    @TestTransaction
    public void findTest() {
        String uri = Fixture.API_ROOT + GymURI.EXERCISES + "/4000";
        System.out.println(uri);
        ExerciseRepresentation exerciseRepresentation = given().queryParam("exerciseId", "4000")
                .when()
                .contentType(ContentType.JSON)
                .get(uri)
                .then()
                .statusCode(200)
                .extract().as(ExerciseRepresentation.class);
        assertEquals(exerciseRepresentation.id, Fixture.getExerciseRepresentation1().id);
        assertEquals(exerciseRepresentation.programId, Fixture.getExerciseRepresentation1().programId);
    }

    @Test
    @TestTransaction
    public void findTestNotFound() {
        String uri = Fixture.API_ROOT + GymURI.EXERCISES + "/8000";
        System.out.println(uri);
        given().queryParam("exerciseId", "8000")
                .when()
                .contentType(ContentType.JSON)
                .get(uri)
                .then()
                .statusCode(404);
    }

}