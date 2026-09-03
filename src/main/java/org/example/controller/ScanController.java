package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.FileReceiverService;
import org.example.utils.filename.FileManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanController {

    private final FileReceiverService service;
    private final FileManager fileManager;

    @Scheduled(fixedDelay = 10000)
    public void scanDirectory() {
        Path processDir = fileManager.getProcessPath();

        if (!Files.exists(processDir)) {

            return;
        }

        try (Stream<Path> paths = Files.list(processDir)) {
            paths.filter(path -> {
                String name = path.getFileName().toString();

                return !name.endsWith(".temp") &&
                        !name.endsWith(".success") &&
                        !name.endsWith(".error") &&
                        !name.endsWith(".in_progress");

            }).forEach(path -> {
                try {
                    log.info("get local file: {}", path.getFileName());

                    service.processFile(path);


                } catch (Exception e) {
                    log.error("Failed to process local file: {}", path, e);

                }

            });


        } catch (IOException e) {

            log.error("Failed to read directory: {}", processDir, e);
        }
    }
}
