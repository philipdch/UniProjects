package com.project.mygym.utils;

import com.project.mygym.domain.User;

import java.util.List;

public interface Security {

    static List<byte[]> hashPassword(String password){
        return null;
    }

    static boolean chechPassword(String givenPassword, User user) {
        return false;
    }
}
