package ru.mealcard.service.format.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mealcard.utils.generate_models.TypeProcedure;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public final class DataForEnrollDTO {
    private final ZonedDateTime sendAt;
    private final ZonedDateTime scheduledDateTime;
    private final TypeProcedure procType;
    private final Iterable<EnrollDTO> records;

}