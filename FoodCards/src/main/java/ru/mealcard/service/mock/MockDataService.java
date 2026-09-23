package ru.mealcard.service.mock;

import lombok.Getter;
import ru.mealcard.Base;
import ru.mealcard.service.format.dto.EnrollDTO;
import ru.mealcard.utils.generate_models.TypeOperation;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MockDataService extends Base {

    @Getter private final static MockDataService instance = new MockDataService();

    private MockDataService() {
    }

    public Iterable<EnrollDTO> generateRecords(int count) {
        return () -> new Iterator<>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < count;
            }

            @Override
            public EnrollDTO next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                currentIndex++;

                String fio = faker.name().fullName();
                String account = faker.number().digits(16);
                TypeOperation type = TypeOperation.fromCode(
                        faker.options().option("DR", "CR", "ZR"));
                int sum = faker.number().numberBetween(100, 100000);

                return new EnrollDTO(fio, account, type, sum);
            }
        };
    }
}