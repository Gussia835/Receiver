package org.example.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.proto.FileChunk;
import org.example.grpc.proto.FileReceiverGrpc;
import org.example.grpc.proto.ResponseGRPC;
import org.example.service.FileReceiverService;
import org.example.utils.filename.FileManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@GrpcService
@Slf4j
public class GrpcController extends FileReceiverGrpc.FileReceiverImplBase {
    private final FileManager fileManager;
    private final FileReceiverService service;

    @Override
    public StreamObserver<FileChunk> upload(StreamObserver<ResponseGRPC> responseObs) {
        return new StreamObserver<>() {


            private String currFilename;
            private Path tempPath;

            @Override
            public void onNext(FileChunk chunk) {
                try {
                    if (currFilename == null) {
                        currFilename = chunk.getFilename();
                        log.info("start grpc receiving for file {}", currFilename);
                    }

                    tempPath = fileManager.appendChunk(currFilename, chunk.getData().toByteArray());
                    log.debug("append chunk in grpc for {}, offset: {}", currFilename, chunk.getOffset());
                } catch (Exception e) {
                    log.error("Failed to write chunk {}", currFilename, e);
                    responseObs.onError(e);
                }
            }

            @Override
            public void onCompleted() {
                try {
                    log.info("Upload completed for file: {}", currFilename);

                    service.processFile(tempPath);

                    responseObs.onNext(ResponseGRPC.newBuilder()
                            .setStatus("SUCCESS")
                            .setFilename(currFilename)
                            .setTotalBytes(Files.size(tempPath))
                            .setMessage("File successfully received and processing started")
                            .build());

                    responseObs.onCompleted();

                } catch (Exception e) {
                    log.error("Failed to finalize upload {}", currFilename, e);
                    responseObs.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("gRPC upload failed", t);

                if (tempPath != null) {


                    try {
                        Files.deleteIfExists(tempPath);
                        log.info("Cleaned up temporary file: {}", tempPath);


                    } catch (Exception ignored) {
                        log.warn("Could not delete temporary file: {}", tempPath);
                    }
                }
            }
        };

    }
}

//    public Mono<ResponseGRPC> upload(Flux<FileChunk> chunks) {
//        return chunks.doOnNext(chunk -> {
//                    fileManager.appendChunk(chunk.getFilename(), chunk.getData().toByteArray());
//                })
//                .then(
//                        Mono.fromCallable(() -> {
//                            return ResponseGRPC.newBuilder()
//                                    .setStatus("SUCCESS")
//                                    .setMessage("File successful received")
//                                    .build();
//                        })
//                )
//
//                .onErrorResume(e -> {
//                    return Mono.just(ResponseGRPC.newBuilder()
//                            .setStatus("ERROR")
//                            .setMessage("cant receive chunk of file " + FileCh.getFilename())
//                            .build()
//                    );
//                });
//    }

