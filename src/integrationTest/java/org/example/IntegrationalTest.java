package org.example;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.liquibase.enabled=false",
        "grpc.server.port=9999"
})
public abstract class IntegrationalTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_pom_db")
            .withUsername("test_pom")
            .withPassword("test_pom_password");


    @Container
    static OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withDatabaseName("XEPDB1")
            .withUsername("GRU")
            .withPassword("gru_password");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        registry.add("receiver.datasource.pom.url", postgres::getJdbcUrl);
        registry.add("receiver.datasource.pom.username", postgres::getUsername);
        registry.add("receiver.datasource.pom.password", postgres::getPassword);

        registry.add("receiver.datasource.gru.url", oracle::getJdbcUrl);
        registry.add("receiver.datasource.gru.username", oracle::getUsername);
        registry.add("receiver.datasource.gru.password", oracle::getPassword);
    }

    @LocalServerPort
    protected int httpPort;

    @BeforeAll
    static void setupRestAssuredLogging() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setUpRestAssuredAddress() {
        RestAssured.port = httpPort;
        RestAssured.baseURI = "http://localhost";
    }
}
