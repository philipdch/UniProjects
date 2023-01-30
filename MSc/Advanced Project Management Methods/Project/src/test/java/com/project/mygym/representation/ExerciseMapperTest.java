package com.project.mygym.representation;

import com.project.mygym.Fixture;
import com.project.mygym.domain.Exercise;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.values.Category;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.Muscles;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ExerciseMapperTest {

    @Inject
    ExerciseMapper mapper;

    private Program getProgram() {
        Set<PhysicalCondition> aimedAt = new HashSet<>();
        aimedAt.add(PhysicalCondition.NORMAL);
        aimedAt.add(PhysicalCondition.FIT);
        Program program = new Program("name", "goals", 10, Difficulty.INTERMEDIATE, aimedAt, 16, 28, 35);
        program.setId(3000L);
        return program;
    }

    @Test
    void toRepresentation() {
        Set<Muscles> muscles = new HashSet<>();
        muscles.add(Muscles.BICEPS);
        muscles.add(Muscles.UPPER_BACK);
        Exercise exercise = new Exercise("name", Category.STRENGTH, "description", muscles, 10, 5, Difficulty.INTERMEDIATE);
        exercise.setProgram(getProgram());
        ExerciseRepresentation exerciseRepresentation = mapper.toRepresentation(exercise);

        mappingAssertions(exercise, exerciseRepresentation);
    }

    @Test
    void toModel() {
        ExerciseRepresentation exerciseRepresentation = Fixture.getExerciseRepresentation1();
        Exercise exercise = mapper.toModel(exerciseRepresentation);
        exercise.setProgram(getProgram());

        mappingAssertions(exercise, exerciseRepresentation);
    }

    private void mappingAssertions(Exercise exercise, ExerciseRepresentation exerciseRepresentation){
        assertEquals(exercise.getName(), exerciseRepresentation.name);
        assertEquals(exercise.getCategory().label, exerciseRepresentation.category);
        assertEquals(exercise.getDescription(), exerciseRepresentation.description);
        assertEquals(exercise.getMusclesTrained().size(), exerciseRepresentation.musclesTrained.size());
        for (Muscles m : exercise.getMusclesTrained())
            assertTrue(exerciseRepresentation.musclesTrained.contains(m.label));
        assertEquals(exercise.getRepetitions(), exerciseRepresentation.repetitions);
        assertEquals(exercise.getTimeRequired(), exerciseRepresentation.timeRequired);
        assertEquals(exercise.getDifficulty().label, exerciseRepresentation.difficulty);
        assertEquals(exercise.getProgram().getId(), exerciseRepresentation.programId);
    }
}
