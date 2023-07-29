package com.project.mygym.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class ProgramRepresentation {
    public Long id;
    public String name;
    public String goals;
    public int frequency;
    public String difficulty;
    public List<String> aimedAt = new ArrayList<>();
    public int ageMin;
    public int ageMax;
    public float cost;
    public Long trainerId;
    public List<Long> exerciseIds = new ArrayList<>();
}
