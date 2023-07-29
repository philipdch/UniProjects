package com.project.mygym.domain;

import com.project.mygym.utils.SystemDateTime;
import com.project.mygym.values.Gender;
import com.project.mygym.values.PhysicalCondition;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("SimpleUser")
public class SimpleUser extends User {

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "height")
    private int height;

    @Column(name = "weight")
    private double weight;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "physical_condition")
    private PhysicalCondition physicalCondition;

    private Integer age;
    private Float bmi;

    @OneToMany(mappedBy="simpleUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy="simpleUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExerciseProgress> exerciseProgresses = new HashSet<>();

    public SimpleUser() { }

    public SimpleUser(String username, String phoneNumber, String emailAddress, Gender gender, int height, double weight, LocalDate dateOfBirth, PhysicalCondition physicalCondition) {
        super(username, phoneNumber, emailAddress);
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.dateOfBirth = dateOfBirth;
        this.physicalCondition = physicalCondition;
        calculateAge();
        calculateBMI();
    }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public int getHeight() { return height; }
    public void setHeight(int height) {
        this.height = height;
        calculateBMI();
    }

    public double getWeight() { return weight; }
    public void setWeight(double weight) {
        this.weight = weight;
        calculateBMI();
    }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        calculateAge();
    }

    public PhysicalCondition getPhysicalCondition() { return physicalCondition; }
    public void setPhysicalCondition(PhysicalCondition physicalCondition) { this.physicalCondition = physicalCondition; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getBmi() { return bmi; }
    public void setBmi(Float bmi) {
        this.bmi = bmi;
    }

    public Set<Subscription> getSubscriptions() { return subscriptions; }

    public Set<ExerciseProgress> getExerciseProgresses() { return exerciseProgresses; }

    public Integer calculateAge() {
        LocalDateTime nowDate = SystemDateTime.now();
        setAge(nowDate.getYear() - dateOfBirth.getYear());
        return age;
    }

    public Float calculateBMI() {
        if (weight <= 0 || height <= 0) return null;
        setBmi((float) (weight / (height * height)));
        return bmi;
    }

    public boolean subscribe(Program program) {
        if (program == null || !isLoggedIn) return false;
        Subscription s = new Subscription(this, program);
        subscriptions.add(s);
        program.getSubscriptions().add(s);
        return true;
    }

    public boolean unsubscribe(Program program) {
        if (program == null || !isLoggedIn) return false;
        subscriptions.removeIf(s -> s.getProgram().equals(program));
        program.getSubscriptions().removeIf(s -> s.getProgram().equals(program));
        return true;
    }

    public boolean startExercise(Exercise exercise) {
        if(!isLoggedIn) return false;
        for (Subscription s: subscriptions) {
            for (Exercise e: s.getProgram().getExercises()) {
                if (e.equals(exercise)) {
                    ExerciseProgress exerciseProgress = new ExerciseProgress(this, exercise);
                    exerciseProgresses.add(exerciseProgress);
                    exercise.getExerciseProgresses().add(exerciseProgress);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updateExercise(Exercise exercise, int repetitionsLeft, double weightOnCompletion) {
        if (!isLoggedIn || repetitionsLeft < 0 || weightOnCompletion <= 0) return false;
        LocalDateTime latest = LocalDateTime.MIN;
        ExerciseProgress latestProgress = null;
        for (ExerciseProgress ep : exerciseProgresses) {
            if (ep.getExercise().equals(exercise) && ep.getId().getCreatedOn().isAfter(latest)) {
                latest = ep.getId().getCreatedOn();
                latestProgress = ep;
            }
        }
        if(latestProgress != null){
            latestProgress.setRepetitionsLeft(repetitionsLeft);
            if (latestProgress.isCompleted()) {
                if (weightOnCompletion <= 0 ) return false;
                latestProgress.setWeightOnCompletion(weightOnCompletion);
            }
            return true;
        }
         return false;
    }

    public ExerciseProgress findExerciseProgress(Exercise exercise, LocalDateTime date) {
        for (ExerciseProgress ep : exerciseProgresses) {
            if (ep.getExercise().equals(exercise) && ep.getId().getCreatedOn().isEqual(date))
                return ep;
        }
        return null;
    }

    public boolean isProgramCompleted(Program program) {
        Set<Exercise> exercisesCompleted = new HashSet<>();
        for (Exercise e: program.getExercises()) {
            for (ExerciseProgress ep: getExerciseProgresses()) {
                if (ep.getExercise().equals(e) && ep.getRepetitionsLeft() == 0)
                    exercisesCompleted.add(e);
            }
        }
        return program.getExercises().size() == exercisesCompleted.size();
    }
}
