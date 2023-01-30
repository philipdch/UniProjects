package com.project.mygym.persistence;

import com.project.mygym.IntegrationBase;
import com.project.mygym.domain.Program;
import com.project.mygym.domain.Trainer;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TrainerJPATest extends IntegrationBase {

    @Inject
    TrainerRepository trainerRepository;

    @Test
    @TestTransaction
    public void persistNewProgram() {
        List<Trainer> results = getInitialTrainer();
        assertEquals(1, results.size());
        Trainer trainer = results.get(0);

        Set<PhysicalCondition> physicalConditionSet = new HashSet<>();
        physicalConditionSet.add(PhysicalCondition.OVERWEIGHT);
        Program newProgram = new Program("Effective weight loss", "A fast-track program intended for fast but controlled weight loss", 4, Difficulty.INTERMEDIATE, physicalConditionSet, 19, 55, 14.99f);

        trainer.createProgram(newProgram);

        Trainer savedTrainer = trainerRepository.findById(trainer.getId());
        assertEquals(3, savedTrainer.getPrograms().size());
    }

    @Test
    @TestTransaction
    public void updateTrainer() {
        List<Trainer> trainers = getInitialTrainer();
        Trainer trainer = trainers.get(0);
        assertEquals(1, trainers.size());

        trainer.setProfileDescription("10 years experience in strength and endurance training. Professional fitness coach.");
        trainer.setPhoneNumber("2105500015");

        trainerRepository.persist(trainer);

        Trainer savedTrainer = trainerRepository.findById(trainer.getId());
        assertEquals("philip_15", savedTrainer.getUsername());
        assertEquals("Guido", savedTrainer.getFirstName());
        assertEquals("Mista", savedTrainer.getLastname());
        assertEquals("2105500015", savedTrainer.getPhoneNumber());
        assertEquals("10 years experience in strength and endurance training. Professional fitness coach.", trainer.getProfileDescription());
    }

    private List<Trainer> getInitialTrainer() {
        return trainerRepository.list("lastname", "Mista");
    }
}
