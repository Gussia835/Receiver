package ru.mealcard.utils.send_models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mealcard.utils.generate_models.TypeOperation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TypeOperationTest {
    @ParameterizedTest
    @CsvSource({"CR", "ZR", "DR"})
    void testOperationTypeFromCode(String code) {
        assertEquals(TypeOperation.valueOf(code), TypeOperation.fromCode(code));
    }

    @ParameterizedTest
    @CsvSource({"pp", "Iao"})
    void testIllegalCodeOperationFromCode(String code) {
        assertThrows(IllegalArgumentException.class, () -> TypeOperation.fromCode(code));
    }
}
