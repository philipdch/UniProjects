package com.project.mygym.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ExerciseTest {

    Program program;
    Exercise exercise;
    SimpleUser simpleUser;

    @BeforeEach
    void setup(){
        program = new Program();
        exercise = new Exercise();
        program.addExercise(exercise);
        simpleUser = new SimpleUser();
        simpleUser.setLoggedIn(true);
        simpleUser.subscribe(program);
        simpleUser.startExercise(exercise);
    }

    @Test
    public void testAssociations(){
        for (ExerciseProgress ep: exercise.getExerciseProgresses()) {
            assertSame(ep.getExercise(), exercise);
        }
        assertEquals(exercise.getExerciseProgresses().size(), 1);
    }

    // check: two new users start the exercise, one of them is subscribed to the exercise's program
    // result: the exercise is associated only with the subscribed user
    @Test
    public void addExerciseProgress() {
        SimpleUser simpleUser2 = new SimpleUser();
        simpleUser2.setLoggedIn(true);
        simpleUser2.subscribe(program);
        simpleUser2.startExercise(exercise);

        SimpleUser simpleUser3 = new SimpleUser();
        simpleUser3.setLoggedIn(true);
        simpleUser3.startExercise(exercise);

        for (ExerciseProgress ep : simpleUser2.getExerciseProgresses()) {
            assertSame(ep.getExercise(), exercise);
        }
        assertEquals(exercise.getExerciseProgresses().size(), 2);
    }
}
