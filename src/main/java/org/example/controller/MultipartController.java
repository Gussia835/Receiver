package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.impl.visitor.validator.EnrollValidator;
import org.example.utils.filename.FileManager;
import org.example.service.FileReceiverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class MultipartController {

    private final FileReceiverService service;
    private final FileManager fileManager;

    private final EnrollValidator validator;

    @PostMapping("multipart")
    public ResponseEntity<String> getMultipart(@RequestParam("file")MultipartFile file) {



        if (file.isEmpty()) {
            log.error("Error: file is empty in multipart");
            return ResponseEntity.badRequest().body("ERROR: file is empty");
        }

        String filename = file.getOriginalFilename();
        log.info("Received multipart file: {}", filename);

        if (!validator.isValidFilename(Paths.get(filename))) {
            log.error("Invalid filename format: {}", filename);
            return ResponseEntity.badRequest().body("Invalid filename format");
        }


        Path filepath = fileManager.writeFile(filename, file);

        service.processFile(filepath);

        return ResponseEntity.ok("File processed successful for multipart");
    }
}
