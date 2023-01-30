package com.project.mygym.representation;

import com.project.mygym.Fixture;
import com.project.mygym.domain.*;
import com.project.mygym.persistence.ProgramRepository;
import com.project.mygym.values.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ExerciseProgressMapperTest {

    @Inject
    ExerciseProgressMapper mapper;

    private Exercise getExercise() {
        Set<Muscles> muscles = new HashSet<>();
        muscles.add(Muscles.BICEPS);
        muscles.add(Muscles.UPPER_BACK);
        Exercise exercise = new Exercise("Dumbell rows", Category.STRENGTH, "Very basic back exercise", muscles, 10, 20, Difficulty.BEGINNER);
        exercise.setId(4000L);
        return exercise;
    }

    private SimpleUser getSimpleUser() {
        SimpleUser simpleUser = new SimpleUser("username", "phoneNumber", "email", Gender.M, 180, 70D, LocalDate.now(), PhysicalCondition.NORMAL);
        simpleUser.setId(1000L);
        return simpleUser;
    }

    @Test
    void toRepresentation() {
        ExerciseProgress exerciseProgress = new ExerciseProgress();
        exerciseProgress.getId().setExerciseId(getExercise().getId());
        exerciseProgress.getId().setUserId(getSimpleUser().getId());
        exerciseProgress.getId().setCreatedOn(LocalDateTime.now());
        exerciseProgress.setRepetitionsLeft(10);
        exerciseProgress.setWeightOnCompletion(70D);

        ExerciseProgressRepresentation exerciseProgressRepresentation = mapper.toRepresentation(exerciseProgress);

        mappingAssertions(exerciseProgress, exerciseProgressRepresentation);
    }

    @Test
    void toModel() {
        ExerciseProgressRepresentation exerciseProgressRepresentation = Fixture.getExerciseProgressRepresentation();
        ExerciseProgress exerciseProgress = mapper.toModel(exerciseProgressRepresentation);

        mappingAssertions(exerciseProgress, exerciseProgressRepresentation);
    }

    private void mappingAssertions(ExerciseProgress exerciseProgress, ExerciseProgressRepresentation exerciseProgressRepresentation){
        assertEquals(exerciseProgress.getId().getUserId(), exerciseProgressRepresentation.userId);
        assertEquals(exerciseProgress.getId().getExerciseId(), exerciseProgressRepresentation.exerciseId);
        assertEquals(exerciseProgress.getId().getCreatedOn(), LocalDateTime.parse(exerciseProgressRepresentation.createdOn));
        assertEquals(exerciseProgress.getRepetitionsLeft(), exerciseProgressRepresentation.repetitionsLeft);
        assertEquals(exerciseProgress.getWeightOnCompletion(), exerciseProgressRepresentation.weightOnCompletion);
        // assertEquals(exerciseProgress.getSimpleUser().getId(), exerciseProgressRepresentation.userId);
        // assertEquals(exerciseProgress.getExercise().getId(), exerciseProgressRepresentation.exerciseId);
    }
}
