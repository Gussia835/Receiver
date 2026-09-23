package ru.mealcard.utils.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import ru.mealcard.utils.encoding.FileEncoding;

public enum ErrorType {
    EMPTY(null),
    SPACES(null),
    NEWLINES(null),

    UTF_16(FileEncoding.UTF_16),
    WINDOWS_1251(FileEncoding.WINDOWS_1251);

    @Getter
    private final FileEncoding encoding;

    ErrorType(FileEncoding encoding) {


        this.encoding = encoding;
    }


}
