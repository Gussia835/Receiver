package org.example.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileStatus {

    IN_PROGRESS(".in_progress", "in_progress"),
    SUCCESS(".success", "success"),
    ERROR(".error", "error");

    private final String extension;
    private final String folderName;



}
