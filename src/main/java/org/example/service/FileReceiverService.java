package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.pom.PomFile;
import org.example.service.dao.SaverDAO;
import org.example.service.impl.visitor.EnrollParserVisitor;
import org.example.service.impl.visitor.validator.EnrollValidator;
import org.example.utils.filename.FileManager;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileReceiverService {
    private final EnrollParserVisitor parserVisitor;
    private final FileManager fileManager;
    private final SaverDAO saver;

    private final EnrollValidator validator;

    public void processFile(Path filepath) {
        String filename = filepath.getFileName().toString();


        PomFile pomFile = PomFile.builder()
                .filename(filename)
                .fullPath(filepath.getParent().toString())
                .fileStatus("IN_PROCESS")
                .uliDate(java.time.format.DateTimeFormatter.ofPattern("yyyyDDD").format(java.time.LocalDate.now()))
                .build();

        pomFile = saver.savePomFile(pomFile);

        boolean isValidHeader = validator.validateHeader(filepath);
        boolean isValidTrailer = validator.validateTrailer(filepath);

        if (!isValidHeader || !isValidTrailer) {
            log.warn("file is not valid header is valid: {}, trailer is valid: {}", isValidHeader, isValidTrailer);
        }

        parserVisitor.setContext(pomFile.getId(), isValidHeader, isValidTrailer);


        try (BufferedReader reader = Files.newBufferedReader(filepath)) {
            String line;


            while ((line = reader.readLine()) != null) {
                parserVisitor.visit(line);
            }

            pomFile.setFileStatus(isValidTrailer && isValidHeader ? "SUCCESS" : "ERROR");
            saver.savePomFile(pomFile);
            fileManager.moveToFileResult(filepath, isValidHeader && isValidTrailer);

        } catch (Exception e) {


            pomFile.setFileStatus("ERROR");
            pomFile.setFileComment(e.getMessage());

            saver.savePomFile(pomFile);

            fileManager.moveToFileResult(filepath, false);
        }
    }
}
