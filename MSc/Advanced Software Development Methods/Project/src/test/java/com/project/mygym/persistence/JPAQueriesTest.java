package com.project.mygym.persistence;

import com.project.mygym.IntegrationBase;
import com.project.mygym.domain.*;
import com.project.mygym.values.Category;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.Gender;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class JPAQueriesTest extends IntegrationBase {

    @Inject
    EntityManager em;

    @Test
    @TestTransaction
    public void queryExercise(){
        Query q = em.createQuery("select e from Exercise e");
        List<Exercise> results = q.getResultList();
        assertEquals(1, results.size());
        Exercise ex1 = results.get(0);
        assertEquals(ex1.getName(), "Bicep Curls");
        assertEquals(Category.STRENGTH, ex1.getCategory());
        assertEquals(25, ex1.getRepetitions());
        assertEquals(10, ex1.getTimeRequired());
        assertEquals(Difficulty.BEGINNER, ex1.getDifficulty());
        assertNotNull(ex1.getProgram());
    }

    @Test
    @TestTransaction
    public void queryProgram(){
        Query q = em.createQuery("select program from Program program");
        List<Program> results = q.getResultList();
        assertEquals(2, results.size());
        Program program = results.get(0);
        assertEquals("First program", program.getName());
        assertEquals(5, program.getFrequency());
        assertEquals(Difficulty.ADVANCED, program.getDifficulty());
        assertEquals(5, program.getCost());
        assertNotNull(program.getTrainer());
        assertEquals(program.getTrainer().getUsername(), "philip_15");
        assertEquals(((Subscription)(program.getSubscriptions().toArray()[0])).getSimpleUser().getUsername(), "arrakis");
    }

    @Test
    @TestTransaction
    public void queryTrainer(){
        Query q = em.createQuery("select trainer from Trainer trainer");
        List<Trainer> results = q.getResultList();
        assertEquals(1, results.size());
        Trainer trainer = results.get(0);
        assertEquals( "philip_15", trainer.getUsername());
        assertEquals(trainer.getEmail(), "philip@gmail.com");
        assertEquals(trainer.getFirstName(), "Guido");
        assertEquals(trainer.getLastname(), "Mista");
        Set<Program> trainerPrograms = trainer.getPrograms();
        assertEquals(trainerPrograms.size(), 2);
        SimpleUser customer = (SimpleUser) em.createQuery("select simpleUser from SimpleUser simpleUser").getResultList().get(0);
        assertEquals(customer.getUsername(), "arrakis");
        assertEquals(customer.getEmail(), "araki@gmail.com");
        assertEquals(customer.getGender(), Gender.M);
        assertEquals(customer.getHeight(), 172);
        assertEquals(customer.getWeight(), 62.5);
        assertEquals(customer.calculateAge(), 24);
        assertEquals(customer.getExerciseProgresses().size(), 2);

        assertEquals(trainer.getCustomerProgress(customer).size(), 1);
    }

    @Test
    @TestTransaction
    public void querySimpleUser(){
        Query q = em.createQuery("select user from SimpleUser user");
        List<SimpleUser> results = q.getResultList();
        assertEquals(results.size(), 1);

        SimpleUser user = results.get(0);
        assertEquals(user.getUsername(), "arrakis");
        assertEquals(user.getEmail(), "araki@gmail.com");
        assertEquals(user.getGender(), Gender.M);
        assertEquals(user.getHeight(), 172);
        assertEquals(user.getWeight(), 62.5);
        assertEquals(user.calculateAge(), 24);
        assertEquals(user.getPhysicalCondition(), PhysicalCondition.NORMAL);
        assertEquals(user.getSubscriptions().size(), 1);
        assertEquals(user.getExerciseProgresses().size(), 2);
        user.getExerciseProgresses().forEach(ep -> System.out.println("createdOn: " + ep.getCreatedOn()));

        Program program = (Program) em.createQuery("select program from Program program").getResultList().get(0);
        assertTrue(user.isProgramCompleted(program));

    }
}

