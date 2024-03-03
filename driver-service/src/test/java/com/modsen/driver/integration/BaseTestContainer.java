package com.modsen.driver.integration;

import com.modsen.driver.constants.ServiceMappings;
import com.modsen.driver.exception.ApiExceptionInfo;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class BaseTestContainer {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.2.0"));

    @Container
    @ServiceConnection
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = ServiceMappings.DRIVER_CONTROLLER;
    }

    protected void extractApiExceptionInfoAndAssert(Response response,
                                                    HttpStatus expectedHttpStatus,
                                                    String exceptedExceptionMessage) {
        ApiExceptionInfo apiExceptionInfo = response
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(exceptedExceptionMessage, apiExceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }
}
