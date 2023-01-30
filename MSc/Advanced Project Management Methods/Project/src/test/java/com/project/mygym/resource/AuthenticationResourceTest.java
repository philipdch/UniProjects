package com.project.mygym.resource;

import com.project.mygym.Fixture;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.persistence.TrainerRepository;
import com.project.mygym.representation.SimpleUserMapper;
import com.project.mygym.representation.SimpleUserRepresentation;
import com.project.mygym.representation.TrainerMapper;
import com.project.mygym.representation.TrainerRepresentation;
import com.project.mygym.utils.SecurityImpl;
import com.project.mygym.values.Gender;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AuthenticationResourceTest {

    @Inject
    SimpleUserRepository simpleUserRepository;

    @Inject
    TrainerRepository trainerRepository;

    @Inject
    SimpleUserMapper simpleUserMapper;

    @Inject
    TrainerMapper trainerMapper;

    SimpleUser registeredUser;
    Trainer registeredTrainer;

    @BeforeEach
    @Transactional
    void setup(){
        registeredTrainer = new Trainer("aragorn.92", "6983740060", "ar_lotr@gmail.com", "Aragorn", "Strider", null, "I am (also) a trainer");

        String trainerPassword = "TiAStr!PW23";
        List<byte[]> hashedPassword1 = SecurityImpl.hashPassword(trainerPassword);
        registeredTrainer.setPassword(hashedPassword1.get(1));
        registeredTrainer.setSalt(hashedPassword1.get(0));

        registeredUser = new SimpleUser("bobman", "6920349856", "bob95@gmail.com", Gender.M, 181, 87.2, LocalDate.of(1995, 10, 25), PhysicalCondition.OVERWEIGHT);

        String userPassword = "NsASngPass10";
        List<byte[]> hashedPassword2 = SecurityImpl.hashPassword(userPassword);
        registeredUser.setPassword(hashedPassword2.get(1));
        registeredUser.setSalt(hashedPassword2.get(0));

        trainerRepository.persist(registeredTrainer);
        simpleUserRepository.persist(registeredUser);
    }

    @AfterEach
    @Transactional
    void tearDown(){
        List<Trainer> results = trainerRepository.list("email", registeredTrainer.getEmail());
        if(results != null && !results.isEmpty()){
            trainerRepository.delete("email", registeredTrainer.getEmail());
        }

        List<SimpleUser> userResults = simpleUserRepository.list("email", registeredUser.getEmail());
        if(userResults != null && !userResults.isEmpty()){
            simpleUserRepository.delete("email", registeredUser.getEmail());
        }
    }

    @Test
    @TestTransaction
    public void successfulSimpleUserRegister(){
        SimpleUserRepresentation newUser = Fixture.getSimpleUserRepresentation2();
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/users/register").build();
        SimpleUserRepresentation created = given().contentType(ContentType.JSON)
                .body(newUser)
                .when().post(uri)
                .then()
                .statusCode(201)
                .extract().as(new TypeRef<>() {});
        assertNotNull(created.id);
        assertNotEquals(created.password, newUser.password); // password is hashed
        assertTrue(created.isLoggedIn);
    }

    @Test
    @TestTransaction
    public void userAlreadyRegistered(){
        SimpleUserRepresentation newUser = Fixture.getSimpleUserRepresentation1();
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/users/register").build();
        given().contentType(ContentType.JSON)
                .body(newUser)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode());
    }

    @Test
    @TestTransaction
    public void registerNoPassword(){
        SimpleUserRepresentation newUser = Fixture.getSimpleUserRepresentation2();
        newUser.password = "";

        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/users/register").build();
        given().contentType(ContentType.JSON)
                .body(newUser)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestTransaction
    public void successfulLogin(){
        SimpleUserRepresentation simpleUserRepresentation = simpleUserMapper.toRepresentation(registeredUser);
        simpleUserRepresentation.password = "NsASngPass10";
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/users/login").build();
        SimpleUserRepresentation returned = given().contentType(ContentType.JSON)
                .body(simpleUserRepresentation)
                .when().post(uri)
                .then()
                .statusCode(200)
                .extract().as(SimpleUserRepresentation.class);
        assertTrue(returned.isLoggedIn);
        assertEquals(returned.email, simpleUserRepresentation.email);
    }

    @Test
    @TestTransaction
    public void loginNonExistentUser(){
        SimpleUserRepresentation simpleUserRepresentation = Fixture.getSimpleUserRepresentation2();
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/users/login").build();
        given().contentType(ContentType.JSON)
                .body(simpleUserRepresentation)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestTransaction
    public void loginWrongPassword(){
        SimpleUserRepresentation simpleUserRepresentation = simpleUserMapper.toRepresentation(registeredUser);
        simpleUserRepresentation.password = "NsASngPass15";
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/users/login").build();
        given().contentType(ContentType.JSON)
                .body(simpleUserRepresentation)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestTransaction
    public void successfulTrainerRegister(){
        TrainerRepresentation newTrainer = Fixture.getNewTrainerRepresentation();
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/trainers/register").build();
        TrainerRepresentation created = given().contentType(ContentType.JSON)
                .body(newTrainer)
                .when().post(uri)
                .then()
                .statusCode(201)
                .extract().as(new TypeRef<>() {});
        assertNotNull(created.id);
        assertNotEquals(created.password, newTrainer.password); // password is hashed
        assertTrue(created.isLoggedIn);
    }

    @Test
    @TestTransaction
    public void trainerAlreadyRegistered(){
        TrainerRepresentation trainer = trainerMapper.toRepresentation(registeredTrainer);
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/trainers/register").build();
        given().contentType(ContentType.JSON)
                .body(trainer)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode());
    }

    @Test
    @TestTransaction
    public void registerTrainerNoPassword(){
        TrainerRepresentation trainer = Fixture.getNewTrainerRepresentation();
        trainer.email = "vangogh@museum.art";
        trainer.password = "";

        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/trainers/register").build();
        given().contentType(ContentType.JSON)
                .body(trainer)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestTransaction
    public void successfulTrainerLogin(){
        TrainerRepresentation trainerRepresentation = trainerMapper.toRepresentation(registeredTrainer);
        trainerRepresentation.password = "TiAStr!PW23";
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/trainers/login").build();
        TrainerRepresentation returned = given().contentType(ContentType.JSON)
                .body(trainerRepresentation)
                .when().post(uri)
                .then()
                .statusCode(200)
                .extract().as(TrainerRepresentation.class);
        assertTrue(returned.isLoggedIn);
        assertEquals(returned.email, trainerRepresentation.email);
    }

    @Test
    @TestTransaction
    public void loginNonExistentTrainer(){
        TrainerRepresentation trainerRepresentation = Fixture.getNewTrainerRepresentation();
        trainerRepresentation.email = "vangosh@museum.art";
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/trainers/login").build();
        given().contentType(ContentType.JSON)
                .body(trainerRepresentation)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestTransaction
    public void loginTrainerWrongPassword(){
        TrainerRepresentation trainerRepresentation = trainerMapper.toRepresentation(registeredTrainer);
        trainerRepresentation.password = "TiAStr!PW";
        URI uri = UriBuilder.fromUri(Fixture.API_ROOT + GymURI.AUTHENTICATION + "/trainers/login").build();
        given().contentType(ContentType.JSON)
                .body(trainerRepresentation)
                .when().post(uri)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
