package ru.mealcard.service.format.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mealcard.utils.generate_models.TypeOperation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollDTO {
    private String fio;
    private String account;
    private TypeOperation type;
    private int summ;
}