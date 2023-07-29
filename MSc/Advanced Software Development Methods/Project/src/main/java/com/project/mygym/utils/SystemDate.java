package com.project.mygym.utils;

import java.time.LocalDate;

public class SystemDate {

    private static LocalDate DateNow;
    private static boolean useCustom;

    public static LocalDate getDateNow() {
        LocalDate _ret = useCustom ? DateNow : LocalDate.now();
        System.out.println(_ret);
        return _ret;
    }

    public static void setDateNow(LocalDate dateNow) {
        DateNow = dateNow;
        useCustom = true;
    }

    public static void resetDateNow() {
        DateNow = null;
        useCustom = false;
    }
}
