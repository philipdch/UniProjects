package com.project.mygym.values;

public enum Muscles {
    CHEST("Chest"),
    UPPER_BACK("Upper back"),
    LOWER_BACK("Lower back"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    UPPER_ABS("Upper abs"),
    LOWER_ABS("Lower abs"),
    THIGHS("Thighs"),
    CALVES("Calves");

    public final String label;

    private Muscles(String muscles){
        this.label = muscles;
    }

    public static Muscles valueOfLabel(String label) {
        for (Muscles muscles : values()) {
            if (muscles.label.equals(label)) {
                return muscles;
            }
        }
        return null;
    }
}
