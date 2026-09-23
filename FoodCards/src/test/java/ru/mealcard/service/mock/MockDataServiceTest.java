package ru.mealcard.service.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.mealcard.service.format.dto.EnrollDTO;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MockDataServiceTest {

    private final MockDataService service = MockDataService.getInstance();

    private EnrollDTO record;

    @BeforeEach
    void setUp() {
        record = service.generateRecords(1).iterator().next();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 50, 200})
    void testGeneratesRequestedCount(int count) {
        List<EnrollDTO> records = new ArrayList<>();
        service.generateRecords(count).forEach(records::add);

        assertThat(records).hasSize(count);
    }

    @Test
    void testAccountIsValid() {
        assertThat(record.getAccount()).matches("[1-9]\\d{15}");
    }

    @Test
    void testSummInRange() {
        assertThat(record.getSumm()).isBetween(100, 100_000);
    }

    @Test
    void testTypeAndFioPresent() {
        assertThat(record.getType()).isNotNull();
        assertThat(record.getFio()).isNotBlank();
    }

    @Test
    void testIterableIsReusable() {
        Iterable<EnrollDTO> records = service.generateRecords(3);
        int first = 0;
        int second = 0;

        for (EnrollDTO r : records) {
            first++;
        }

        for (EnrollDTO r : records) {
            second++;
        }

        assertThat(first).isEqualTo(3);
        assertThat(second).isEqualTo(3);
    }

    @Test
    void testEachCallGeneratesNewRecords() {
        EnrollDTO r1 = service.generateRecords(1).iterator().next();
        EnrollDTO r2 = service.generateRecords(1).iterator().next();

        assertThat(r1.getAccount()).isNotEqualTo(r2.getAccount());
    }

    @Test
    void testTypeOperationIsOneOfAllowed() {
        for (int i = 0; i < 100; i++) {
            EnrollDTO r = service.generateRecords(1).iterator().next();
            String code = r.getType().getCode();

            assertThat(code).isIn("DR", "CR", "ZR");
        }
    }

    @Test
    void testZeroCountReturnsEmptyIterable() {
        List<EnrollDTO> records = new ArrayList<>();
        service.generateRecords(0).forEach(records::add);

        assertThat(records).isEmpty();

    }
}
