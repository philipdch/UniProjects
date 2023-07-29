package com.project.mygym.values;

import com.project.mygym.utils.SystemDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ExerciseUserId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "created_on")
    private LocalDateTime createdOn; // = SystemDateTime.now();

    public ExerciseUserId(){}

    public Long getUserId() {
        return userId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    /* Override equals and hashcode so that Hibernate will be notified of object state changes */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseUserId that = (ExerciseUserId) o;
        return userId.equals(that.userId) && exerciseId.equals(that.exerciseId) && createdOn.equals(that.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, exerciseId, createdOn);
    }
}
