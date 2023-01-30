package com.project.mygym.representation;

import com.project.mygym.domain.ExerciseProgress;
import com.project.mygym.domain.Subscription;
import com.project.mygym.values.Gender;
import com.project.mygym.values.PhysicalCondition;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RegisterForReflection
public class SimpleUserRepresentation {
    public Long id;
    public String email;
    public String username;
    public String phoneNumber;
    public String password;
    public Gender gender;
    public int height;
    public double weight;
    public String dateOfBirth;
    public String physicalCondition;
    public boolean isLoggedIn;
}
