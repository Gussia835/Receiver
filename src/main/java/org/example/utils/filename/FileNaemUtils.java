package org.example.utils.filename;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.example.utils.enums.FileStatus;

import java.io.File;

@UtilityClass
public class FileNaemUtils {

    public String removeExtension(String filename) {
        for (FileStatus status : FileStatus.values()) {
            StringUtils.removeEnd(filename, status.getExtension());
        }

        return filename;
    }

    public String addExtension(String filename, FileStatus status) {

        return StringUtils.appendIfMissingIgnoreCase(filename, status.getExtension());

    }

    public String replaceExtension(String filename, FileStatus newStatus) {

        String orig = removeExtension(filename);

        return addExtension(orig, newStatus);
    }
}
