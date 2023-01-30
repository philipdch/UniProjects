package com.project.mygym.domain;

import com.project.mygym.values.PhysicalCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class ProgramTest {

    Program program;
    Exercise exercise1;
    Exercise exercise2;
    SimpleUser simpleUser;

    @BeforeEach
    void setup(){
        program = new Program();
        exercise1 = new Exercise();
        program.addExercise(exercise1);
        exercise2 = new Exercise();
        program.addExercise(exercise2);
        simpleUser = new SimpleUser();
        simpleUser.setLoggedIn(true);
        simpleUser.subscribe(program);
    }

    @Test
    public void testAssociations(){
        for(Exercise e: program.getExercises()){
            assertSame(e.getProgram(), program);
        }
        assertEquals(program.getExercises().size(), 2);
        assertTrue(program.getExercises().contains(exercise1));
        assertTrue(program.getExercises().contains(exercise2));

        for (Subscription s : program.getSubscriptions()) {
            assertSame(s.getProgram(), program);
        }
        assertEquals(program.getSubscriptions().size(), 1);
    }

    // check: add a regural exercise to a program
    // result: the exercise is associated with the program
    @Test
    public void addRegularExercise(){
        Exercise ex = new Exercise();
        program.addExercise(ex);
        assertTrue(program.getExercises().contains(ex));
        assertEquals(program.getExercises().size(), 3);
        for(Exercise e: program.getExercises())
            assertEquals(e.getProgram(), program);
    }

    // check: add a null exercise to a program
    // result: the exercise is NOT associated with the program
    @Test
    public void addNullExercise(){
        program.addExercise(null);
        assertEquals(program.getExercises().size(), 2);
    }

    // check: add the exercise of a program to another program
    // result: the exercise is associated with the new program and NOT with the old
    @Test
    public void addSameExerciseToDifferentProgram() {
        Program newProgram = new Program();
        newProgram.addExercise(exercise1);
        assertEquals(newProgram.getExercises().size(), 1);
        assertTrue(newProgram.getExercises().contains(exercise1));
        assertFalse(program.getExercises().contains(exercise1));
        for (Exercise e : newProgram.getExercises())
            assertSame(e.getProgram(), newProgram);
    }

    @Test
    public void userCanSubscribe(){
        simpleUser.setAge(18);
        simpleUser.setPhysicalCondition(PhysicalCondition.FIT);

        program.setAgeMin(18);
        program.setAgeMax(25);
        Set<PhysicalCondition> physicalConditionSet = new HashSet<>();
        physicalConditionSet.add(PhysicalCondition.FIT);
        program.setAimedAt(physicalConditionSet);

        boolean canSubscribe = program.canSubscribe(simpleUser);
        assertTrue(canSubscribe);
    }

    @Test
    public void userCannotSubscribe(){
        simpleUser.setAge(17);
        simpleUser.setPhysicalCondition(PhysicalCondition.FIT);

        program.setAgeMin(18);
        program.setAgeMax(25);
        Set<PhysicalCondition> physicalConditionSet = new HashSet<>();
        physicalConditionSet.add(PhysicalCondition.FIT);
        program.setAimedAt(physicalConditionSet);

        boolean canSubscribe = program.canSubscribe(simpleUser);
        assertFalse(canSubscribe);
    }
}
