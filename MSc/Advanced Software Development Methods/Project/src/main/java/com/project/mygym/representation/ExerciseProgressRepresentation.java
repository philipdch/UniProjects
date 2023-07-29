package com.project.mygym.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class ExerciseProgressRepresentation {
    public Long exerciseId;
    public Long userId;
    public String createdOn;
    public int repetitionsLeft;
    public double weightOnCompletion;
}
