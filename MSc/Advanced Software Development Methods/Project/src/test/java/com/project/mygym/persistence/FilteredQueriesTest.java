package com.project.mygym.persistence;

import com.project.mygym.domain.Program;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.values.Gender;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class FilteredQueriesTest {

    SimpleUser user;

    @Inject
    ProgramRepository programRepository;

    @BeforeEach
    void setup(){
        user = new SimpleUser("Bob", "6976928344", "Bob.10@gmail.com", Gender.M, 189, 96, LocalDate.of(1986, 4, 11), PhysicalCondition.NORMAL);
    }

    @Test
    @Transactional
    public void testGetAllPrograms(){
        List<Program> foundPrograms = programRepository.listAll();
        assertEquals(foundPrograms.size(), 2);
    }

    @Test
    @Transactional
    public void testTailoredPrograms1(){
        List<Program> tailoredPrograms = programRepository.findTailoredPrograms(user);
        assertEquals(2, tailoredPrograms.size());
    }

    @Test
    @Transactional
    public void testTailoredPrograms2(){
        user.setPhysicalCondition(PhysicalCondition.SKINNY);
        List<Program> tailoredPrograms2 = programRepository.findTailoredPrograms(user);
        assertEquals(1, tailoredPrograms2.size());
    }

    @Test
    @Transactional
    public void testNameQuery(){
        List<Program> results = programRepository.findProgramsByName("first");
        assertEquals(1, results.size());
    }

    @Test
    @Transactional
    public void testNameNotExists(){
        List<Program> results = programRepository.findProgramsByName("null");
        assertEquals(0, results.size());
    }

    @Test
    @Transactional
    public void testFindByAge(){
        List<Program> results = programRepository.findProgramsByAge(15);
        assertEquals(1, results.size());

        results = programRepository.findProgramsByAge(90);
        assertEquals(1, results.size());
    }

    @Test
    @Transactional
    public void testAgeOutOfBounds(){
        List<Program> results = programRepository.findProgramsByAge(14);
        assertEquals(0, results.size());

        results = programRepository.findProgramsByAge(91);
        assertEquals(0, results.size());
    }

//    @Test
//    public void testFindByGoals(){
//        List<Program> results = FilteredQueries.findProgramByGoals("arms core");
//        assertEquals(1, results.size());
//
//        results = FilteredQueries.findProgramByGoals("strengthen");
//        assertEquals(1, results.size());
//
//        results = FilteredQueries.findProgramByGoals("bicep");
//        assertEquals(0, results.size());
//    }
}
