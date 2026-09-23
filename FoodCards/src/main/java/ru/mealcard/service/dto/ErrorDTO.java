package ru.mealcard.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDTO {
    private final String status;
    private final String error;
}
