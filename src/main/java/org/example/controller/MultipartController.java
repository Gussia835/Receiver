package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.filename.FileManager;
import org.example.service.FileReceiverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class MultipartController {

    private final FileReceiverService service;
    private final FileManager fileManager;

    @PostMapping("multipart")
    public ResponseEntity<String> getMultipart(@RequestParam("filename") String filename,
                                               @RequestParam("file")MultipartFile file) {



        if (file.isEmpty()) {
            log.error("error file is empty in multipart");
            return ResponseEntity.badRequest().body("ERROR: file is empty");
        }


        log.info("Received multipart file: {}", file.getOriginalFilename());


        Path filepath = fileManager.writeFile(filename, file);


        Path targetPath = fileManager.moveToInProgress(filepath);
        service.processFile(targetPath);

        return ResponseEntity.ok("File processed successful for multipart");
    }
}
