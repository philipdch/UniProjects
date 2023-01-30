package com.project.mygym.domain;

import com.project.mygym.utils.SystemDate;
import com.project.mygym.utils.SystemDateTime;
import io.quarkus.test.TestTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class SimpleUserTest {

    SimpleUser simpleUser;
    Program program1;
    Exercise exercise1;
    LocalDateTime testDate;

    @BeforeEach
    void setup() {
        simpleUser = new SimpleUser();
        simpleUser.setLoggedIn(true);
        program1 = new Program();
        exercise1 = new Exercise();
        program1.addExercise(exercise1);
        testDate = LocalDateTime.of(1999, 10, 10, 17, 30);
        SystemDateTime.setNow(testDate);
        simpleUser.subscribe(program1);
        simpleUser.startExercise(exercise1);
    }

    @Test
    public void testAssociations(){
        for(Subscription s: simpleUser.getSubscriptions()){
            assertSame(s.getSimpleUser(), simpleUser);
        }
        assertEquals(simpleUser.getSubscriptions().size(), 1);

        for(ExerciseProgress ep: simpleUser.getExerciseProgresses()){
            assertSame(ep.getSimpleUser(), simpleUser);
        }
        assertEquals(simpleUser.getExerciseProgresses().size(), 1);
    }

    // check: subscribe a user to another program
    // result: the user is associated with the other program
    @Test
    public void addSubscription() {
        Program program2 = new Program();
        simpleUser.subscribe(program2);
        for (Subscription s : simpleUser.getSubscriptions()) {
            assertSame(s.getSimpleUser(), simpleUser);
        }
        assertEquals(simpleUser.getSubscriptions().size(), 2);
    }

    // check: a user subscribes to a new null program
    // result: the user is NOT associated with the new program
    @Test
    public void addNullSubscription() {
        simpleUser.subscribe(null);
        assertEquals(simpleUser.getSubscriptions().size(), 1);
    }

    // check: unsubscribe a user from a program
    // result: the user is NOT associated with the program anymore
    @Test
    public void removeSubscription() {
        simpleUser.unsubscribe(program1);
        assertEquals(simpleUser.getSubscriptions().size(), 0);
    }

    // check: a user starts two new exercises, one part of the program and one not
    // result: the user is associated only with the exercise of the program
    @Test
    public void addExercise() {
        Exercise exercise2 = new Exercise();
        program1.addExercise(exercise2);
        boolean success = simpleUser.startExercise(exercise2);
        assertTrue(success);

        assertEquals(simpleUser.getExerciseProgresses().size(), 2);
    }

    @Test
    public void addNonExistentExercise(){
        Exercise exercise3 = new Exercise();
        boolean success = simpleUser.startExercise(exercise3);
        assertFalse(success);
        assertEquals(simpleUser.getExerciseProgresses().size(), 1);
    }

    // check: a user updates an exercise's progress
    // result: the exercise's progress is updated correctly
    @Test
    public void updateExercise() {
        int repetitionsLeft = 0;
        int weightOnCompletion = 65;
        boolean successfulUpdate = simpleUser.updateExercise(exercise1, repetitionsLeft, weightOnCompletion);
        assertTrue(successfulUpdate);
        assertEquals(simpleUser.findExerciseProgress(exercise1, SystemDateTime.now()).getRepetitionsLeft(), repetitionsLeft);
        assertEquals(simpleUser.findExerciseProgress(exercise1, SystemDateTime.now()).getWeightOnCompletion(), 65);
    }

    @Test
    public void updateNonExistentExercise(){
        Exercise exercise3 = new Exercise();
        boolean success = simpleUser.updateExercise(exercise3, 5, 60);
        assertFalse(success);
        assertEquals(1, simpleUser.getExerciseProgresses().size());
    }

    @Test
    public void updateExerciseInvalidInput() {
        boolean successfulUpdate = simpleUser.updateExercise(exercise1, -1, 65);
        assertFalse(successfulUpdate);
        successfulUpdate = simpleUser.updateExercise(exercise1, 3, 0);
        assertFalse(successfulUpdate);
        successfulUpdate = simpleUser.updateExercise(exercise1, -1, 0);
        assertFalse(successfulUpdate);
    }

    @Test
    public void findExistingExerciseProgress(){
        ExerciseProgress ep = simpleUser.findExerciseProgress(exercise1, SystemDateTime.now());
        assertNotNull(ep);
    }

    @Test
    public void findNonExistingProgress(){
        ExerciseProgress ep = simpleUser.findExerciseProgress(null, SystemDateTime.now());
        assertNull(ep);
    }

    // check: a user completes all exercises of a program before one more is added
    // result: the program is completed only before the new exercise is added
    @Test
    public void completeProgram() {
        assertTrue(simpleUser.isProgramCompleted(program1));
        program1.addExercise(new Exercise());
        assertFalse(simpleUser.isProgramCompleted(program1));
    }

    @Test
    public void notLoggedInOperations(){
        simpleUser.setLoggedIn(false);
        Program program2 = new Program();
        boolean success = simpleUser.subscribe(program2);
        assertFalse(success);
        assertEquals(1, simpleUser.getSubscriptions().size());

        success = simpleUser.unsubscribe(program1);
        assertFalse(success);
        assertEquals(1, simpleUser.getSubscriptions().size());

        Exercise exercise2 = new Exercise();
        program1.addExercise(exercise2);
        success = simpleUser.startExercise(exercise2);
        assertFalse(success);
        assertEquals(1, simpleUser.getExerciseProgresses().size());

        success = simpleUser.updateExercise(exercise1, 5, 0);
        assertFalse(success);
        assertEquals(1, simpleUser.getExerciseProgresses().size());
    }

    @AfterEach
    public void clear() { SystemDateTime.reset(); }

}
