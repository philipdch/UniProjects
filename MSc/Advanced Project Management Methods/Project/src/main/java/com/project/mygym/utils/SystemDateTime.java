package com.project.mygym.utils;

import java.time.LocalDateTime;

public class SystemDateTime {

    private static LocalDateTime stub;

    public static LocalDateTime now() {
        return stub == null ? LocalDateTime.now() : stub;
    }

    public static void setNow(LocalDateTime now) {
        stub = now;
    }

    public static void reset() {
        stub = null;
    }

}
