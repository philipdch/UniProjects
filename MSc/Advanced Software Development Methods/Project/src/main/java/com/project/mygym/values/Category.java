package com.project.mygym.values;

public enum Category {
    ENDURANCE("Endurance"),
    STRENGTH("Strength"),
    BALANCE("Balance"),
    FLEXIBILITY("Flexibility");

    public final String label;

    private Category(String cat){
        this.label = cat;
    }

    public static Category valueOfLabel(String label) {
        for (Category cat : values()) {
            if (cat.label.equals(label)) {
                return cat;
            }
        }
        return null;
    }
}
