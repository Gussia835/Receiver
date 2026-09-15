package org.example;

import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.example.models.pom.PomFile;
import org.example.models.pom.PomUnit;
import org.example.repository.pom.PomFileRepository;
import org.example.repository.pom.PomUnitRepository;
import org.example.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ChunkIntegrationTest extends IntegrationalTest {

    @Autowired
    private PomFileRepository pomFileRepository;

    @Autowired
    private PomUnitRepository pomUnitRepository;

    private final Faker faker = new Faker();
    private String validFilename;
    private String validFileContent;
    private String invalidFilename;

    private static final Charset FILE_CHARSET = Charset.forName("windows-1251");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");

    @BeforeEach
    void setUpValidTestData() {
        String bankCode = "001";
        String branchCode = "032";
        String systemName = "GLAER";
        int sequence = faker.number().numberBetween(1, 999);
        int uliDate = faker.number().numberBetween(100, 999);
        validFilename = String.format("Z%s%s.%s_ENROLL%s%s%d.%03d",
                bankCode, branchCode, systemName,
                bankCode, branchCode, sequence, uliDate);

        String currentTime = LocalDateTime.now().format(DATE_FORMATTER);
        String account = "1000" + faker.number().digits(12);
        String fio = faker.name().fullName();
        int amount = faker.number().numberBetween(100, 10000);

        String header = String.format("H %s IMMEDIATE               ", currentTime);
        String body = String.format("%-100s%-30s%-2s%20s", fio, account, "DR", amount);
        String trailer = "T         1         ";

        validFileContent = header + "\r\n" + body + "\r\n" + trailer + "\r\n";
    }

    @BeforeEach
    void setUpInvalidTestData() {
        invalidFilename = "invalid_filename.txt";
    }

    @BeforeEach
    void cleanDatabase() {
        pomUnitRepository.deleteAll();
        pomFileRepository.deleteAll();
    }

    @Test
    void testValidChunk() {
        given()
                .spec(requestSpec)
                .header("filename", validFilename) // Имя файла в заголовке
                .contentType(ContentType.BINARY)   // Тип данных: сырые байты
                .body(validFileContent.getBytes(FILE_CHARSET))
                .when()
                .post("/files/chunk")
                .then()
                .statusCode(Constants.HTTP_STATUS_OK);

        Optional<PomFile> savedFile = pomFileRepository.findByFilename(validFilename);
        assertThat(savedFile).isPresent();
        assertThat(savedFile.get().getFileStatus()).isEqualTo(Constants.STATUS_SUCCESS);

        List<PomUnit> units = pomUnitRepository.findAll();
        assertThat(units).hasSize(3);
    }

    @Test
    void testInvalidFilename() {
        given()
                .spec(requestSpec)
                .header("filename", invalidFilename)
                .contentType(ContentType.BINARY)
                .body(validFileContent.getBytes(FILE_CHARSET))
                .when()
                .post("/files/chunk")
                .then()
                .statusCode(Constants.HTTP_STATUS_BAD_REQUEST);

        Optional<PomFile> savedFile = pomFileRepository.findByFilename(invalidFilename);
        assertThat(savedFile).isEmpty();
    }

    @Test
    void testMissingFilenameHeader() {
        given()
                .spec(requestSpec)
                .contentType(ContentType.BINARY)
                .body(validFileContent.getBytes(FILE_CHARSET))
                .when()
                .post("/files/chunk")
                .then()
                .statusCode(Constants.HTTP_STATUS_BAD_REQUEST); // Spring вернет 400, так как @RequestHeader обязателен
    }
}