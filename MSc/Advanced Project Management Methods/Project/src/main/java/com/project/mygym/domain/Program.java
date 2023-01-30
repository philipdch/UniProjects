package com.project.mygym.domain;

import com.project.mygym.values.Difficulty;
import com.project.mygym.values.PhysicalCondition;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "programs")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    private String goals;

    @Column(name = "frequency", nullable = false)
    private int frequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @ElementCollection(targetClass = PhysicalCondition.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "program_conditions",
            joinColumns = @JoinColumn(name = "program_id"),
            foreignKey = @ForeignKey(
                    name = "program_fk",
                    foreignKeyDefinition = "FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE")
    )
    private Set<PhysicalCondition> aimedAt = new HashSet<>();

    @Column(name = "ageMin", nullable = false)
    private int ageMin;

    @Column(name = "ageMax", nullable = false)
    private int ageMax;

    @Column(name = "cost", nullable = false)
    private float cost;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "program", fetch = FetchType.LAZY)
    private final Set<Exercise> exercises = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Subscription> subscriptions = new HashSet<>();

    public Program() {
    }

    public Program(String name, String goals, int frequency, Difficulty difficulty, Set<PhysicalCondition> aimedAt, int ageMin, int ageMax, float cost) {
        this.name = name;
        this.goals = goals;
        this.frequency = frequency;
        this.difficulty = difficulty;
        this.aimedAt = aimedAt;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Set<PhysicalCondition> getAimedAt() {
        return aimedAt;
    }

    public void setAimedAt(Set<PhysicalCondition> aimedAt) {
        this.aimedAt = aimedAt;
    }

    public void addPhysicalCondition(PhysicalCondition physCond) {
        aimedAt.add(physCond);
    }

    public void removePhysicalCondition(PhysicalCondition physCond) {
        aimedAt.remove(physCond);
    }

    public int getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(int ageMin) {
        this.ageMin = ageMin;
    }

    public int getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(int ageMax) {
        this.ageMax = ageMax;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public Set<Exercise> getExercises() {
        return exercises;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer newTrainer) {
        Trainer oldTrainer = this.trainer;
        if(oldTrainer != null){
            oldTrainer.getPrograms().remove(this);
        }
        this.trainer = newTrainer;
        if(newTrainer != null){
            newTrainer.getPrograms().add(this);
        }
    }

    public void addExercise(Exercise exercise) {
        if (exercise != null) {
            exercise.setProgram(this);
        }
    }

    public void removeExercise(Exercise exercise) {
        exercise.setProgram(null);
    }

    public boolean canSubscribe(SimpleUser newUser){
        return aimedAt.contains(newUser.getPhysicalCondition()) && newUser.getAge() >= ageMin && newUser.getAge() <= ageMax;
    }
}

