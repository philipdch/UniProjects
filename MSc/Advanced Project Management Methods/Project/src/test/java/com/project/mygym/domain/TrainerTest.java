package com.project.mygym.domain;

import com.project.mygym.utils.SystemDateTime;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.PhysicalCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerTest {

    Trainer trainer;
    Program program;
    SimpleUser user;

    @BeforeEach
    void setup(){
        trainer = new Trainer();
        trainer.setLoggedIn(true);
        program = new Program();
        user = new SimpleUser();
        user.setLoggedIn(true);
        trainer.createProgram(program);
    }

    @Test
    public void testAssociations(){
        for(Program program: trainer.getPrograms()){
            assertSame(program.getTrainer(), trainer);
        }
        assertEquals(trainer.getPrograms().size(), 1);
        assertTrue(trainer.getPrograms().contains(program));
    }

    // check: trainer creates a new program
    // result: the trainer is associated with both programs (old and new)
    @Test
    public void testCreateProgram(){
        Set<PhysicalCondition> conditionSet = new HashSet<>();
        conditionSet.add(PhysicalCondition.FIT);
        Program program2 = new Program("Second Program", "Intensive cardio and endurance training", 3, Difficulty.ADVANCED, conditionSet, 17, 65, 10.9f);
        trainer.createProgram(program2);
        assertEquals(trainer.getPrograms().size(), 2);
        for(Program program: trainer.getPrograms()){
            assertSame(program.getTrainer(), trainer);
        }
        assertTrue(trainer.getPrograms().contains(program2));
    }

    // check: trainer creates a new null program
    // result: the trainer is NOT associated with the new program
    @Test
    public void testAddNullProgram(){
        trainer.createProgram(null);
        assertEquals(trainer.getPrograms().size(), 1);
    }

    // check: trainer creates the same program again
    // result: the trainer is associated with the program only once
    @Test
    public void testAddSameTrainerToSameProgram(){
        trainer.createProgram(program);
        assertEquals(trainer.getPrograms().size(),1);
        assertSame(program.getTrainer(), trainer);
    }

    @Test
    public void calculateIncomeSpecificMonth(){
        SystemDateTime.setNow(LocalDateTime.of(2022, Month.MAY, 14, 17, 30));
        program.setCost(10);
        Program program2 = new Program();
        program2.setCost(5);
        trainer.createProgram(program2);
        user.subscribe(program2);

        SimpleUser user2 = new SimpleUser();
        user2.setLoggedIn(true);
        user2.subscribe(program);

        for(Subscription subs: user.getSubscriptions()){
            System.out.println(subs.getCreatedOn());
        }
        assertEquals(BigDecimal.valueOf(15.0), trainer.calculateIncome(Month.MAY));

        SystemDateTime.reset();
    }

    @Test
    public void calculateIncomeNoSubscriptions(){
        assertEquals(trainer.calculateIncome(Month.FEBRUARY), BigDecimal.ZERO);
    }

    // check: a user starts an exercise in one of trainer's programs
    // result: the trainer receives the user's progress in all exercises
    @Test
    public void getCustomersProgress() {
        Exercise exercise = new Exercise();
        program.addExercise(exercise);
        user.subscribe(program);
        user.startExercise(exercise);

        Program program2 = new Program();
        Exercise exercise2 = new Exercise();
        program2.addExercise(exercise2);
        trainer.createProgram(program2);
        user.subscribe(program2);
        user.startExercise(exercise2);

        assertEquals(trainer.getCustomerProgress(user).size(), 2);
    }

    // check: a user starts an exercise in a program of another trainer
    // result: the trainer does NOT receive the user's progress
    @Test
    public void getCustomerProgressNoCustomers() {
        SimpleUser user2 = new SimpleUser();
        Program program2 = new Program();
        Exercise exercise2 = new Exercise();
        program2.addExercise(exercise2);
        user2.subscribe(program2);
        user2.startExercise(exercise2);

        assertEquals(trainer.getCustomerProgress(user).size(), 0);
    }
}
