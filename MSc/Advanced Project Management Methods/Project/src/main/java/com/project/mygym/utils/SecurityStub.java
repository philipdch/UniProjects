package com.project.mygym.utils;

import com.project.mygym.domain.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SecurityStub implements Security{

    public static byte[] salt = "g283vdhu38".getBytes(StandardCharsets.UTF_8);
    public static byte[] hash = "@8f3tv9vdh8wef!".getBytes(StandardCharsets.UTF_8);

    public static List<byte[]> hashPassword(String password){
        List<byte[]> hashedPassword = new ArrayList<>();
        hashedPassword.add(salt);
        hashedPassword.add(hash);
        return hashedPassword;
    }

    public static boolean checkPassword(String givenPassword, User user){
        return user.getPassword() == hash && user.getSalt() == salt;
    }
}
