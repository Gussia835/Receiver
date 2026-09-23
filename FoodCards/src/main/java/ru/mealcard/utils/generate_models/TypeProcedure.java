package ru.mealcard.utils.generate_models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TypeProcedure {
    IN_TIME("IN-TIME"),
    IMMEDIATE("IMMEDIATE");

    private final String code;

    public String getCode() {
        return code;
    }

    public static TypeProcedure fromCode(String code) {
        switch (code.toUpperCase()) {
            case "IN-TIME" -> {
                return IN_TIME;
            }
            default -> {
                return IMMEDIATE;
            }
        }
    }

}
