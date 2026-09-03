package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.filename.FileManager;
import org.example.service.FileReceiverService;
import org.example.service.impl.visitor.validator.EnrollValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class ChunkController {
    private final FileReceiverService service;
    private final FileManager fileManager;
    private final EnrollValidator validator;

    @PostMapping("/chunk")
    public ResponseEntity<String> getChunk(@RequestParam("filename") String filename,
                                           @RequestParam(value = "isLast", required = true) Boolean isLast,
                                           @RequestBody byte[] chunk
                                           ) {
        try {
            log.debug("Received chunk for {}, size: {} bytes, isLast: {}", filename, chunk.length, isLast);

            Path tempPath = fileManager.appendChunk(filename, chunk);

            if (isLast) {
                log.info("Last chunk received for {}. Finalizing file...", filename);

                Path targetPath = tempPath.resolveSibling(filename);
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                Path inProgressPath = fileManager.moveToInProgress(targetPath);
                service.processFile(inProgressPath);

                return ResponseEntity.ok("File fully uploaded and processing started");
            }

            return ResponseEntity.accepted().body("Chunk accepted");

        } catch (IOException e) {
            log.error("Failed to process chunk for file: {}", filename, e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }

    }

}
