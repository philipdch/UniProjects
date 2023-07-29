package com.project.mygym.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class TrainerRepresentation {
    public Long id;
    public String email;
    public String username;
    public String phoneNumber;
    public String password;
    public String firstName;
    public String lastname;
    public String profileDescription;
    public List<Long> programIds = new ArrayList<>();
    public boolean isLoggedIn;
}
