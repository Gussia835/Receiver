package ru.mealcard.utils.generate_models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TypeOperation {
    DR("DR"),
    CR("CR"),
    ZR("ZR");

    private final String code;

    public String getCode() {
        return code;
    }

    public static TypeOperation fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Type code cannot be null");
        }
        return switch (code.toUpperCase()) {
            case "DR" -> DR;
            case "CR" -> CR;
            case "ZR" -> ZR;
            default ->
                throw new IllegalArgumentException("Unknown operation type: " + code);
        };
    }
}
