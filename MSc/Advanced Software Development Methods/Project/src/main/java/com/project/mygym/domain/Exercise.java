package com.project.mygym.domain;

import com.project.mygym.values.Category;
import com.project.mygym.values.Difficulty;
import com.project.mygym.values.Muscles;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "description", length = 200)
    private String description;

    @ElementCollection(targetClass = Muscles.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "muscles_trained",
            joinColumns = @JoinColumn(name = "exercise_id"),
            foreignKey = @ForeignKey(
                    name = "exercise_fk",
                    foreignKeyDefinition = "FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE")
    )
    private Set<Muscles> musclesTrained = new HashSet<>();

    @Column(name = "repetitions", nullable = false)
    private int repetitions;

    @Column(name = "time_required")
    private int timeRequired;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExerciseProgress> exerciseProgresses = new HashSet<>();

    public Exercise() {
    }

    public Exercise(String name, Category category, String description, Set<Muscles> musclesTrained, int repetitions, int timeRequired, Difficulty difficulty) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.musclesTrained = musclesTrained;
        this.repetitions = repetitions;
        this.timeRequired = timeRequired;
        this.difficulty = difficulty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program newProgram) {
        Program oldProgram = this.program;
        if (oldProgram != null) {
            oldProgram.getExercises().remove(this);
        }
        this.program = newProgram;
        if (newProgram != null) {
            newProgram.getExercises().add(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Muscles> getMusclesTrained() {
        return musclesTrained;
    }

    public void setMusclesTrained(Set<Muscles> musclesTrained) {
        this.musclesTrained = musclesTrained;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(int timeRequired) {
        this.timeRequired = timeRequired;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Set<ExerciseProgress> getExerciseProgresses() {
        return exerciseProgresses;
    }


}
