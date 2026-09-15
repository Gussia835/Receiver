package org.example;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import net.devh.boot.grpc.server.config.GrpcServerProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
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
        registry.add("receiver.datasource.gru.url", oracle::getJdbcUrl);
    }

    @LocalServerPort
    protected int httpPort;

    @Autowired
    protected GrpcServerProperties grpcPort;

    @BeforeAll
    static void setupRestAssuredLogging() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected RequestSpecification requestSpec;

    @BeforeEach
    void setUpRestAssuredAddress() {
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(this.httpPort)
                .build();
    }
}
