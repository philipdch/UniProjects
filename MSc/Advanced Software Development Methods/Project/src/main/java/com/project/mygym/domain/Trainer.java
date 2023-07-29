package com.project.mygym.domain;

import javax.persistence.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("Trainer")
public class Trainer extends User{

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "profilePic")
    private File profilePicture;

    @Column(name = "description")
    private String profileDescription;

//    @Embedded
//    private Credentials credentials;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "trainer")
    private Set<Program> programs = new HashSet<>();

    public Trainer() {
    }

    public Trainer(String username, String phoneNumber, String emailAddress, String firstName, String lastname, File profilePicture, String profileDescription) {
        super(username, phoneNumber, emailAddress);
        this.firstName = firstName;
        this.lastname = lastname;
        this.profilePicture = profilePicture;
        this.profileDescription = profileDescription;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public File getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(File profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    public void createProgram(Program program) {
        if(!isLoggedIn) return;
        if(program != null)
            program.setTrainer(this);
    }

    public BigDecimal calculateIncome(Month month) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        for(Program program: programs){
            BigDecimal programIncome = BigDecimal.ZERO;
            for(Subscription sub: program.getSubscriptions()){
                if(sub.getCreatedOn().getMonth().equals(month)){
                    programIncome = programIncome.add(BigDecimal.valueOf(program.getCost()));
                }
            }
            totalIncome = totalIncome.add(programIncome);
        }
        return totalIncome;
    }

    public Set<ExerciseProgress> getCustomerProgress(SimpleUser simpleUser) {
        Set<ExerciseProgress> exerciseProgresses = new HashSet<>();
        for (Program p: getPrograms()) {
            for (Subscription s: p.getSubscriptions()) {
                if (s.getSimpleUser().equals(simpleUser)) {
                    for (ExerciseProgress ep: s.getSimpleUser().getExerciseProgresses()) {
                        if (ep.getRepetitionsLeft() == 0 && ep.getExercise().getProgram().getTrainer().equals(this)) {
                            exerciseProgresses.add(ep);
                        }
                    }
                }
            }
        }
        return exerciseProgresses;
    }
}
