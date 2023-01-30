package com.project.mygym.resource;

import com.project.mygym.Fixture;
import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.ExerciseProgress;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.representation.ExerciseProgressRepresentation;
import com.project.mygym.representation.ExerciseRepresentation;
import com.project.mygym.representation.ProgramRepresentation;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.UriBuilder;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class TrainerResourceTest {

    @Test
    @TestTransaction
    public void createNewProgram() {
        ProgramRepresentation programRepresentation = Fixture.getNewProgramRepresentation();

        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.TRAINERS + "/" + Fixture.getTrainerRepresentation().id).build();

        ProgramRepresentation created = given().contentType(ContentType.JSON)
                .body(programRepresentation)
                .when().post(uri)
                .then()
                .statusCode(201)
                .extract().as(new TypeRef<>() {
                });
        assertNotNull(created.id);
        assertNotNull(created.trainerId);
        assertEquals(created.name, Fixture.getNewProgramRepresentation().name);
    }

    @Test
    @TestTransaction
    public void createProgramNonexistentTrainer() {
        ProgramRepresentation programRepresentation = Fixture.getNewProgramRepresentation();
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.TRAINERS + "/0").build();

        given().contentType(ContentType.JSON)
                .body(programRepresentation)
                .when().post(uri)
                .then()
                .statusCode(404);
    }

    @Test
    @TestTransaction
    public void calculateMonthlyIncome() {
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.TRAINERS + "/" + Fixture.getTrainerRepresentation().id + "/income").build();
        String income = given().queryParam("month", 5).when().get(uri)
                .then()
                .statusCode(200)
                .extract().asString();
        assertEquals(BigDecimal.valueOf(Double.parseDouble(income)), BigDecimal.valueOf(5.0));
    }

    @Test
    @TestTransaction
    public void calculateAllMonths(){
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.TRAINERS + "/" + Fixture.getTrainerRepresentation().id + "/income").build();
        String income = when().get(uri)
                .then()
                .statusCode(200)
                .extract().asString();
        assertEquals(BigDecimal.valueOf(Double.parseDouble(income)), BigDecimal.valueOf(5.0));
    }

    @Test
    @TestTransaction
    public  void calculateIncomeNoTrainer(){
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.TRAINERS + "/0" + "/income").build();
        when().get(uri)
                .then()
                .statusCode(404);
    }

    @Test
    @TestTransaction
    public void getCustomerProgress(){
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.TRAINERS + "/" + Fixture.getTrainerRepresentation().id + "/customerProgress/" + Fixture.getSimpleUserRepresentation1().id).build();

        Set<ExerciseProgressRepresentation> results = when().get(uri)
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>(){});

        assertNotNull(results);
        assertEquals(1, results.size());
        for(ExerciseProgressRepresentation exp: results){
            assertEquals(exp.userId, Fixture.getSimpleUserRepresentation1().id);
        }
    }
}
