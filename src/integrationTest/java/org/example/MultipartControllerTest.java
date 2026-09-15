package org.example;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;
import net.datafaker.Faker;
import org.example.models.gru.GruVistaTab;
import org.example.models.pom.PomFile;
import org.example.models.pom.PomUnit;
import org.example.models.pom.PomUnitError;
import org.example.repository.gru.GruVistaTabRepository;
import org.example.repository.pom.PomFileRepository;
import org.example.repository.pom.PomUnitErrorRepository;
import org.example.repository.pom.PomUnitRepository;
import org.example.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import javax.swing.text.html.Option;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MultipartControllerTest extends IntegrationalTest {

    @Autowired
    private PomFileRepository pomFileRepository;

    @Autowired
    private PomUnitRepository pomUnitRepository;

    @Autowired
    private GruVistaTabRepository gruVistaTabRepository;

    @Autowired
    private PomUnitErrorRepository pomUnitErrorRepository;

    private final Faker faker = new Faker();
    private String validFilename;
    private String validFileContent;
    private String invalidFilename;
    private String invalidFileContent;

    private static final String CONTENT_TYPE = "text/plain";
    private static final Charset FILE_CHARSET = Charset.forName("windows-1251");
    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");

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

        String header = String.format("H %s IMMEDIATE               ",
                currentTime);
        String body = String.format("%-100s%-30s%-2s%20s",
                fio, account, "DR", amount);
        String trailer = "T         1         ";

        validFileContent = header + "\r\n" +
                body + "\r\n" +
                trailer + "\r\n";

        String invalidHeader = String.format("X %s IMMEDIATE               ",
                currentTime);
        invalidFileContent = invalidHeader + "\r\n" +
                                body + "\r\n" +
                                trailer + "\r\n";
    }

    @BeforeEach
    void setUpInvalidTestData() {
        invalidFilename = "invalid_filename.txt";
    }

    @BeforeEach
    void cleanDatabase() {
        pomUnitErrorRepository.deleteAll();
        gruVistaTabRepository.deleteAll();
        pomUnitRepository.deleteAll();
        pomFileRepository.deleteAll();
    }

    @Test
    void testValidMultipart() {
        MultiPartSpecification multipart = new MultiPartSpecBuilder(validFileContent.getBytes(FILE_CHARSET))
                                            .controlName("file")
                                            .fileName(validFilename)
                                            .mimeType(CONTENT_TYPE)
                                            .build();
        given()
                .spec(requestSpec)
                .multiPart(multipart)
                .when()
                .post("/files/multipart")
                .then()
                .statusCode(Constants.HTTP_STATUS_OK)
                .body(equalTo(Constants.MSG_MULTIPART_SUCCESS));

        Optional<PomFile> savedFile = pomFileRepository.findByFilename(validFilename);
        assertThat(savedFile).isPresent();
        assertThat(savedFile.get().getFileStatus()).isEqualTo(Constants.STATUS_SUCCESS);

        List<PomUnit> units = pomUnitRepository.findAll();
        assertThat(units).hasSize(3);

        List<GruVistaTab> grus = gruVistaTabRepository.findAll();
        assertThat(grus).hasSize(1);
    }

    @Test
    void testInvalidFilename() {
        MultiPartSpecification multipart = new MultiPartSpecBuilder(validFileContent.getBytes(FILE_CHARSET))
                .controlName("file")
                .fileName(invalidFilename)
                .mimeType(CONTENT_TYPE)
                .build();

        given()
                .spec(requestSpec)
                .multiPart(multipart)
                .when()
                .post("/files/multipart")
                .then()
                .statusCode(Constants.HTTP_STATUS_BAD_REQUEST)
                .body(equalTo(Constants.MSG_INVALID_FILENAME));

        Optional<PomFile> pomFiles = pomFileRepository.findByFilename(invalidFilename);
        assertThat(pomFiles).isEmpty();
    }


    @Test
    void testEmptyFile() {
        MultiPartSpecification multipart = new MultiPartSpecBuilder(new byte[0])
                .controlName("file")
                .fileName(validFilename)
                .mimeType(CONTENT_TYPE)
                .build();

        given()
                .spec(requestSpec)
                .multiPart(multipart)
                .when()
                .post("/files/multipart")
                .then()
                .statusCode(Constants.HTTP_STATUS_BAD_REQUEST)
                .body(equalTo(Constants.MSG_FILE_EMPTY));

        Optional<PomFile> savedFile = pomFileRepository.findByFilename(validFilename);
        assertThat(savedFile).isEmpty();
    }

    @Test
    void testInvalidContentFile() {
        MultiPartSpecification multipart = new MultiPartSpecBuilder(invalidFileContent.getBytes(FILE_CHARSET))
                .controlName("file")
                .fileName(validFilename)
                .mimeType(CONTENT_TYPE)
                .build();

        given()
                .spec(requestSpec)
                .multiPart(multipart)
                .when()
                .post("/files/multipart")
                .then()
                .statusCode(Constants.HTTP_STATUS_OK);

        Optional<PomFile> savedFile = pomFileRepository.findByFilename(validFilename);
        assertThat(savedFile).isPresent();
        assertThat(savedFile.get().getFileStatus()).isEqualTo(Constants.STATUS_ERROR);

        List<PomUnit> unit = pomUnitRepository.findAll();
        assertThat(unit).hasSize(3);

        List<PomUnitError> errors = pomUnitErrorRepository.findAll();
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getFileId()).isEqualTo(savedFile.get().getId());

        List<GruVistaTab> grus = gruVistaTabRepository.findAll();
        assertThat(grus).isEmpty();
    }
}
