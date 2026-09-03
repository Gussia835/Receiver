package org.example.utils.filename;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.Properties;
import org.example.exception.FileProcessingException;
import org.example.exception.ReceivingFileException;
import org.example.utils.enums.FileStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileManager {
    private final Properties properties;


    public Path getProcessPath() {
        return Paths.get(properties.getProcessDir());
    }

    public Path appendChunk(String filename, byte[] chunk) {
        try {


            Path dir = Paths.get(properties.getProcessDir());
            Files.createDirectories(dir);

            Path tempPath = dir.resolve(filename + ".temp");

            Files.write(tempPath, chunk, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            return tempPath;

        } catch (IOException e) {
            log.error("error of appending chunk");
            throw new ReceivingFileException("error of appending chunk");
        }

    }

    public Path writeFile(String filename, MultipartFile file) {
        Path dir = Paths.get(properties.getProcessDir());
        try {
            Files.createDirectories(dir);
            Path filepath = dir.resolve(filename + ".temp");

            file.transferTo(filepath.toFile());

            return  filepath;
        } catch (IOException e) {
            throw new ReceivingFileException("receive chunk exception" + file.toString());
        }
    }


    public Path moveToInProgress(Path sourcePath) {
        return moveToStatus(sourcePath, FileStatus.IN_PROGRESS);
    }

    public void moveToFileResult(Path inProgressPath, boolean isSuccess) {
        FileStatus status = isSuccess ? FileStatus.SUCCESS : FileStatus.ERROR;
        moveToStatus(inProgressPath, status);
    }



    private Path moveToStatus(Path filepath, FileStatus status) {

        Path target = getCurrDir(status);

        String filename = filepath.getFileName().toString();
        String newFilename = FileNaemUtils.replaceExtension(filename, status);

        Path targetFile = target.resolve(newFilename);

        try {
            Files.createDirectories(target);
            Files.move(filepath, targetFile, StandardCopyOption.ATOMIC_MOVE);

            return targetFile;

        } catch (IOException e) {
            throw new FileProcessingException("Cannot move file: " + e.getMessage());
        }
    }


    private Path getCurrDir(FileStatus status) {
        String dir = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        return Path.of(properties.getTargetDir(), dir, status.getFolderName());
    }

}
