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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ProgramMapperTest {

    @Inject
    ProgramMapper mapper;

    private Exercise getExercise(){
        Set<Muscles> muscles = new HashSet<>();
        muscles.add(Muscles.BICEPS);
        muscles.add(Muscles.UPPER_BACK);
        Exercise exercise = new Exercise("Dumbell rows", Category.STRENGTH, "Very basic back exercise", muscles, 10, 20, Difficulty.BEGINNER);
        exercise.setId(2000L);
        return exercise;
    }

    private Trainer getTrainer() {
        Trainer trainer = new Trainer("Polnareff44", "6972487343", "jeanp3@luckyland.com", "Jean-Pierre", "Polnareff", null, "I'm an experienced personal trainer, always ready to take up new challenges and train aspiring athletes");
        trainer.setId(2000L);
        return trainer;
    }

    @Test
    void toRepresentation(){

        Set<PhysicalCondition> aimedAt = new HashSet<>();
        aimedAt.add(PhysicalCondition.NORMAL);
        aimedAt.add(PhysicalCondition.FIT);
        Program program = new Program("First Program", "Effective legs workout", 2, Difficulty.INTERMEDIATE, aimedAt, 19, 50, 25.0f);
        program.setTrainer(getTrainer());
        program.addExercise(getExercise());
        ProgramRepresentation programRepresentation = mapper.toRepresentation(program);

        assertEquals(program.getName(), programRepresentation.name);
        assertEquals(program.getAimedAt().size(), programRepresentation.aimedAt.size());
        for(PhysicalCondition physicalCondition: program.getAimedAt()){
            assertTrue(programRepresentation.aimedAt.contains(physicalCondition.name()));
        }
        assertEquals(program.getCost(), programRepresentation.cost);
        assertEquals(program.getFrequency(), programRepresentation.frequency);
        assertEquals(program.getDifficulty().name(), programRepresentation.difficulty);
        assertEquals(program.getTrainer().getId(), programRepresentation.trainerId);
        assertEquals(program.getAgeMax(), programRepresentation.ageMax);
        assertEquals(program.getAgeMin(), programRepresentation.ageMin);
        assertEquals(program.getGoals(), programRepresentation.goals);
        assertEquals(program.getExercises().size(), programRepresentation.exerciseIds.size());
        for(Exercise exercise: program.getExercises()){
            assertTrue(programRepresentation.exerciseIds.contains(exercise.getId()));
        }
    }

    @Test
    void toModel(){
        ProgramRepresentation programRepresentation = Fixture.getProgramRepresentation();

        Program program = mapper.toModel(programRepresentation);
        program.setTrainer(getTrainer());

        mappingAssertions(program, programRepresentation);
    }

    private void mappingAssertions(Program program, ProgramRepresentation programRepresentation){
        assertEquals(program.getName(), programRepresentation.name);
        assertEquals(program.getAimedAt().size(), programRepresentation.aimedAt.size());
        for(PhysicalCondition physicalCondition: program.getAimedAt()){
            assertTrue(programRepresentation.aimedAt.contains(physicalCondition.label));
        }
        assertEquals(program.getCost(), programRepresentation.cost);
        assertEquals(program.getFrequency(), programRepresentation.frequency);
        assertEquals(program.getDifficulty().label, programRepresentation.difficulty);
        assertEquals(program.getAgeMax(), programRepresentation.ageMax);
        assertEquals(program.getAgeMin(), programRepresentation.ageMin);
        assertEquals(program.getGoals(), programRepresentation.goals);
        assertEquals(program.getTrainer().getId(), programRepresentation.trainerId);
    }
}
