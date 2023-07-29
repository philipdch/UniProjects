package com.project.mygym.security;

import com.project.mygym.domain.SimpleUser;
import com.project.mygym.domain.Trainer;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.persistence.TrainerRepository;
import com.project.mygym.utils.SecurityImpl;
import com.project.mygym.utils.SecurityStub;
import com.project.mygym.values.Gender;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javax.inject.Inject;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AuthenticatorTest {

    @Inject
    TrainerRepository trainerRepository;

    @Inject
    SimpleUserRepository simpleUserRepository;

    @Inject
    Authentication authenticator;
    SimpleUser registeredUser;
    Trainer registeredTrainer;

    @BeforeEach
    @Transactional
    void setup() {

        registeredUser = new SimpleUser("bobman", "6920349856", "bob95@gmail.com", Gender.M, 181, 87.2, LocalDate.of(1995, 10, 25), PhysicalCondition.OVERWEIGHT);

        registeredTrainer = new Trainer("aragorn.92", "6983740060", "ar_lotr@gmail.com", "Aragorn", "Strider", null, "I am (also) a trainer");

        String trainerPassword = "TiAStr!PW23";
        List<byte[]> hashedPassword1 = SecurityImpl.hashPassword(trainerPassword);
        registeredTrainer.setPassword(hashedPassword1.get(1));
        registeredTrainer.setSalt(hashedPassword1.get(0));

        String userPassword = "NsASngPass10";
        List<byte[]> hashedPassword2 = SecurityImpl.hashPassword(userPassword);
        registeredUser.setPassword(hashedPassword2.get(1));
        registeredUser.setSalt(hashedPassword2.get(0));

        trainerRepository.persist(registeredTrainer);
        simpleUserRepository.persist(registeredUser);
    }

    @AfterEach
    @Transactional
    void tearDown(){
        List<Trainer> results = trainerRepository.list("email", registeredTrainer.getEmail());
        if(results != null && !results.isEmpty()){
            trainerRepository.delete("email", registeredTrainer.getEmail());
        }

        List<SimpleUser> userResults = simpleUserRepository.list("email", registeredUser.getEmail());
        if(userResults != null && !userResults.isEmpty()){
            simpleUserRepository.delete("email", registeredUser.getEmail());
        }
    }

    @Test
    @TestTransaction
    public void testSuccessfulLogin() {
        boolean successfulLogin = authenticator.login("bob95@gmail.com", "NsASngPass10");
        assertTrue(successfulLogin);
        registeredUser = simpleUserRepository.findById(registeredUser.getId());
        assertTrue(registeredUser.isLoggedIn());
    }

    @Test
    @TestTransaction
    public void testNullLogin() {
        boolean nullLogin = authenticator.login("Nonexistent@gmail.com", "abc122354");
        assertFalse(nullLogin);
    }

    @Test
    void testWrongPassword() {
        boolean successfulLogin = authenticator.login("bob95@gmail.com", "NsA");
        assertFalse(successfulLogin);
        assertFalse(registeredUser.isLoggedIn());
    }

    @Test
    void testSuccessfulRegister() {
        SimpleUser newUser = new SimpleUser("vsauce", "6920894735", "vsauce@gmail.com", Gender.M, 175, 78.9, LocalDate.of(1989, 4, 8), PhysicalCondition.NORMAL);
        newUser.setPassword("12345pass!".getBytes(StandardCharsets.UTF_8));
        boolean successfulRegister = authenticator.register(newUser);
        assertTrue(successfulRegister);
        SimpleUser foundUser = simpleUserRepository.findById(newUser.getId());
        assertEquals(foundUser.getUsername(), "vsauce");
    }

    @Test
    @TestTransaction
    public void testRegisterNoPassword() {
        Trainer trainer = new Trainer("trish_45", "2102345001", "spice.girl@training.gr", "Trish", "Una", null, "Certified Personal Trainer. Specialized in Functional training and Pilates.");
        boolean unsuccessfulRegister = authenticator.register(trainer);
        assertFalse(unsuccessfulRegister);
    }

    @Test
    @TestTransaction
    public void testNullRegister() {
        assertFalse(authenticator.register(null));
    }

    @Test
    @TestTransaction
    public void testInvalidEmailRegister() {
        SimpleUser newUser = new SimpleUser("vsauce", "6920894735", "vsauceinvalid@gmail", Gender.M, 175, 78.9, LocalDate.of(1989, 4, 8), PhysicalCondition.NORMAL);
        newUser.setPassword("1234598765".getBytes(StandardCharsets.UTF_8));
        boolean unsuccessfulLogin = authenticator.register(newUser);
        assertFalse(unsuccessfulLogin);
    }
}
