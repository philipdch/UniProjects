package com.project.mygym.domain;

import com.project.mygym.utils.SystemDate;
import com.project.mygym.utils.SystemDateTime;
import com.project.mygym.values.ExerciseUserId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "exercise_progress")
public class ExerciseProgress {

    @EmbeddedId
    public ExerciseUserId id = new ExerciseUserId();

    @Column(name = "repetitions_left", nullable = false)
    private int repetitionsLeft;

    @Column(name = "weight_on_completion")
    private double weightOnCompletion;

    @ManyToOne(fetch= FetchType.LAZY)
    @MapsId("userId")
    private SimpleUser simpleUser;

    @ManyToOne(fetch= FetchType.LAZY)
    @MapsId("exerciseId")
    private Exercise exercise;

    public ExerciseProgress(SimpleUser simpleUser, Exercise exercise) {
        this.simpleUser = simpleUser;
        this.exercise = exercise;
        this.id.setCreatedOn(SystemDateTime.now());
    }

    public ExerciseProgress() {

    }

    public ExerciseUserId getId() {
        return id;
    }

    public LocalDateTime getCreatedOn() {
        return id.getCreatedOn();
    }

    public int getRepetitionsLeft() {
        return repetitionsLeft;
    }

    public void setRepetitionsLeft(int repetitionsLeft) {
        this.repetitionsLeft = repetitionsLeft;
    }

    public SimpleUser getSimpleUser() {
        return simpleUser;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        this.simpleUser = simpleUser;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public double getWeightOnCompletion() {
        return weightOnCompletion;
    }

    public void setWeightOnCompletion(double weightOnCompletion) {
        this.weightOnCompletion = weightOnCompletion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseProgress that = (ExerciseProgress) o;
        return simpleUser.equals(that.simpleUser) && exercise.equals(that.exercise) && getCreatedOn().equals(that.getCreatedOn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleUser, exercise, getCreatedOn());
    }

    public boolean isCompleted() { return repetitionsLeft == 0; }
}
