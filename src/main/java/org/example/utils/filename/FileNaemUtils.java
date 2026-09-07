package org.example.utils.filename;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.example.utils.enums.FileStatus;

import java.io.File;

@UtilityClass
public class FileNaemUtils {

    public String removeExtension(String filename) {
        String res = filename;
        for (FileStatus status : FileStatus.values()) {
            if (res.endsWith(status.getExtension())) {
                res = res.substring(0, res.length() - status.getExtension().length());
                break;
            }
        }

        return res;
    }

    public String addExtension(String filename, FileStatus status) {

        if (filename.endsWith(status.getExtension())) {
            return filename;
        }
        return filename + status.getExtension();

    }

    public String replaceExtension(String filename, FileStatus newStatus) {

        String orig = removeExtension(filename);

        return addExtension(orig, newStatus);
    }
}
