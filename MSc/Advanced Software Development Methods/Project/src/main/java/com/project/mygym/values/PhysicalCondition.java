package com.project.mygym.values;

public enum PhysicalCondition {
    SKINNY("Skinny"),
    NORMAL("Normal"),
    FIT("Fit"),
    OVERWEIGHT("Overweight");

    public final String label;

    private PhysicalCondition(String physCond){
        this.label = physCond;
    }

    public static PhysicalCondition valueOfLabel(String label) {
        for (PhysicalCondition physCond : values()) {
            if (physCond.label.equals(label)) {
                return physCond;
            }
        }
        return null;
    }
}
