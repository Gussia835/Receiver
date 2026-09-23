package ru.mealcard;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class MockTest extends IntegrationTest {

    private static final int ROWCOUNT = 1000;
    private static final int FILECOUNT = 2;

    private static final String ENDPOINT = "/mock";

    private static String generateValidBody (int fileCount, int rowCount) {
        return """
                {
                  "bankCode": "001",
                  "branchCode": "032",
                  "nameSystem": "GLAER",
                  "rowCount": %d,
                  "fileCount": %d
                }
                """.formatted(rowCount, fileCount);
    }

    @Test
    void testValidBodyRequest() {
        List<String> filenames = given()
                .contentType(ContentType.JSON)
                .body(generateValidBody(FILECOUNT, ROWCOUNT))

                .when()
                .post(ENDPOINT)

                .then()
                .statusCode(200)
                .body("status", equalTo("SUCCESS"))
                .body("filenames", hasSize(2))

                .extract()
                .path("filenames");

        for (String filename : filenames) {
            Path file = outputDir.resolve(filename);
            assertThat(file).exists();

            List<String> lines = linesOf(file);

            assertThat(lines).hasSize(ROWCOUNT + 2);
            assertThat(lines.get(0)).hasSize(42).startsWith("H ");
            assertThat(lines.get(1)).hasSize(152);
            assertThat(lines.get(1001)).hasSize(20).startsWith("T ").endsWith("1000");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"bankCode\":\"12ab\",\"branchCode\":\"032\",\"nameSystem\":\"GLAER\",\"rowCount\":5,\"fileCount\":1}",
            "{\"bankCode\":\"001\",\"branchCode\":\"032\",\"nameSystem\":\"GLAER\",\"rowCount\":0,\"fileCount\":1}",
            "{\"bankCode\":\"001\",\"branchCode\":\"032\",\"nameSystem\":\"GLAER\",\"rowCount\":5,\"fileCount\":-1}",
            "{broken json"
    })
    void testInvalidBodyRequest(String body) {
        given()
                .contentType(ContentType.JSON)
                .body(body)

                .when()
                .post(ENDPOINT)

                .then()
                .statusCode(400)
                .body("status", equalTo("ERROR"));
    }

    @Test
    void testWrongMethodRequest() {
        given()
                .contentType(ContentType.JSON)
                .body(generateValidBody(1, 1))

                .when()
                .get(ENDPOINT)

                .then()
                .statusCode(405);
    }


}
