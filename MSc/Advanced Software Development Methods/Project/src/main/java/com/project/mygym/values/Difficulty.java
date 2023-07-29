package com.project.mygym.values;

public enum Difficulty {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    public final String label;

    private Difficulty(String difficulty){
        this.label = difficulty;
    }

    public static Difficulty valueOfLabel(String label) {
        for (Difficulty diff : values()) {
            if (diff.label.equals(label)) {
                return diff;
            }
        }
        return null;
    }
}
