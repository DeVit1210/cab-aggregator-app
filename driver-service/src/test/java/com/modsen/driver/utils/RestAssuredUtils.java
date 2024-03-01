package com.modsen.driver.utils;

import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.given;

@UtilityClass
public class RestAssuredUtils {
    public static Response findAllDriversResponse() {
        return given()
                .when()
                .get();
    }

    public static Response findDriverByIdResponse(Long driverId) {
        return given()
                .when()
                .get("/" + driverId);
    }

    public static Response createDriverResponse(DriverRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post();
    }

    public static Response updateDriverResponse(Long driverId, DriverRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/" + driverId);
    }

    public static Response deleteDriverResponse(Long driverId) {
        return given()
                .when()
                .delete("/" + driverId);
    }

    public static Response getDriverAvailabilityResponse() {
        return given()
                .when()
                .get("/availability");
    }

    public static Response changeDriverStatusResponse(ChangeDriverStatusRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/status");
    }
}
