package com.project.mygym.representation;

import com.project.mygym.Fixture;
import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.Trainer;
import com.project.mygym.values.Category;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.Muscles;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TrainerMapperTest {

    @Inject
    TrainerMapper mapper;

    private Program getProgram() {
        Set<PhysicalCondition> aimedAt = new HashSet<>();
        aimedAt.add(PhysicalCondition.NORMAL);
        aimedAt.add(PhysicalCondition.FIT);
        Program program1 = new Program("name", "goals", 10, Difficulty.INTERMEDIATE, aimedAt, 16, 28, 35);
        program1.setId(3000L);
        //Program program2 = new Program("name", "goals", 10, Difficulty.INTERMEDIATE, aimedAt, 16, 28, 35);
        //program2.setId(4000L);
        //Set<Program> programs = new HashSet<>();
        //programs.add(program1); programs.add(program2);
        return program1;
    }

    @Test
    void toRepresentation() {
        Trainer trainer = new Trainer("username", "phoneNumber", "emailAddress", "firstName", "lastName", new File(""), "profileDescription");
        trainer.setId(3000L);
        trainer.setPassword("324356".getBytes());
        trainer.createProgram(getProgram());
        TrainerRepresentation trainerRepresentation = mapper.toRepresentation(trainer);

        mappingAssertions(trainer, trainerRepresentation);
    }

    @Test
    void toModel() {
        TrainerRepresentation trainerRepresentation = Fixture.getTrainerRepresentation();

        Trainer trainer = mapper.toModel(trainerRepresentation);
        trainer.setLoggedIn(true);
        trainer.createProgram(getProgram());
        trainer.setId(2000L);

        mappingAssertions(trainer, trainerRepresentation);
    }

    private void mappingAssertions(Trainer trainer, TrainerRepresentation trainerRepresentation){
        assertEquals(trainer.getId(), trainerRepresentation.id);
        assertEquals(trainer.getUsername(), trainerRepresentation.username);
        assertEquals(trainer.getPhoneNumber(), trainerRepresentation.phoneNumber);
        assertEquals(trainer.getEmail(), trainerRepresentation.email);
        assertEquals(trainer.getFirstName(), trainerRepresentation.firstName);
        assertEquals(trainer.getLastname(), trainerRepresentation.lastname);
        assertEquals(trainer.getProfileDescription(), trainerRepresentation.profileDescription);
        assertEquals(trainer.getPrograms().size(), trainerRepresentation.programIds.size());
        for (Program p : trainer.getPrograms())
            assertTrue(trainerRepresentation.programIds.contains(p.getId()));
    }
}
