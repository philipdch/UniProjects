package com.project.mygym.persistence;

import com.project.mygym.IntegrationBase;
import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.values.*;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ProgramJPATest extends IntegrationBase {

    @Inject
    EntityManager em;

    @Inject
    ProgramRepository programRepository;

    @Test
    @TestTransaction
    public void persistNewExercise(){

        List<Program> results = getInitialProgram();
        assertEquals(1, results.size());
        Program program = results.get(0);

        Set<Muscles> musclesTrained = new HashSet<>();
        musclesTrained.add(Muscles.TRICEPS);
        Exercise newExercise = new Exercise("Dumbell tricep extensions", Category.STRENGTH, "Basic tricep exercise", musclesTrained, 20, 20, Difficulty.INTERMEDIATE);

        program.addExercise(newExercise);
        Program savedProgram = programRepository.findById(program.getId());
        assertEquals(2, savedProgram.getExercises().size());
    }

    @Test
    @TestTransaction
    public void findExerciseByName(){
        Query query = em.createQuery("select exercise from Exercise exercise where lower(exercise.name) like '%curls'");
        List<Exercise> exercises = query.getResultList();
        assertEquals(1, exercises.size());
    }

    @Test
    @TestTransaction
    public void updateProgram(){
        List<Program> results = getInitialProgram();
        assertEquals(1, results.size());
        Program program = results.get(0);

        program.setName("First program improved");
        program.setAgeMin(20);
        program.setAgeMax(50);

        program.removePhysicalCondition(PhysicalCondition.NORMAL);
        program.addPhysicalCondition(PhysicalCondition.FIT);
        program.setCost(20.5f);
        program.setDifficulty(Difficulty.INTERMEDIATE);
        program.setFrequency(6);

        programRepository.persist(program);

        Program savedProgram = programRepository.findById(program.getId());
        assertEquals("First program improved", savedProgram.getName());
        assertEquals(20.5f, savedProgram.getCost());
        assertEquals(6, savedProgram.getFrequency());
        assertEquals(Difficulty.INTERMEDIATE, savedProgram.getDifficulty());
        assertEquals(20, savedProgram.getAgeMin());
        assertEquals(50, savedProgram.getAgeMax());
        assertTrue(savedProgram.getAimedAt().contains(PhysicalCondition.FIT));
    }

    private List<Program> getInitialProgram(){
        return programRepository.list("name", "First program");
    }
}
