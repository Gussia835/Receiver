package ru.mealcard;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.mealcard.config.Config;
import ru.mealcard.config.App;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class IntegrationTest {

    protected static Config config = Config.getInstance();
    protected static Path outputDir;



    @BeforeAll
    static void startServer() {
        outputDir = config.getOutputDir();

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = config.getPort();

        App.start();
    }

    @AfterAll
    static void stopServer() {
        App.stop();
    }

    @BeforeEach
    void createFiles() throws IOException {
        clean(outputDir);
        Files.createDirectories(outputDir);
    }

    protected static void clean(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }

        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

}
