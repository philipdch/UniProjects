package com.project.mygym.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class ExerciseRepresentation {
    public Long id;
    public String name;
    public String category;
    public String description;
    public List<String> musclesTrained = new ArrayList<>();
    public int repetitions;
    public int timeRequired;
    public String difficulty;
    public Long programId;
}
