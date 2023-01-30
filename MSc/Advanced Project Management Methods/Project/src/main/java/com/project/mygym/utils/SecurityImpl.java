package com.project.mygym.utils;

import com.project.mygym.domain.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecurityImpl implements Security{

    public static List<byte[]> hashPassword(String password){
        List<byte[]> result = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            result.add(salt);
            result.add(hashedPassword);
        }catch(NoSuchAlgorithmException ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static boolean checkPassword(String givenPassword, User user){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(user.getSalt());
            byte[] hashedPassword = md.digest(givenPassword.getBytes(StandardCharsets.UTF_8));
            return Arrays.equals(hashedPassword, user.getPassword());
        }catch(NoSuchAlgorithmException ex){
            ex.printStackTrace();
            return false;
        }
    }
}
