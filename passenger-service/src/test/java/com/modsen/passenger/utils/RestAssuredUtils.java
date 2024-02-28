package com.modsen.passenger.utils;

import com.modsen.passenger.dto.request.PassengerRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.given;

@UtilityClass
public class RestAssuredUtils {
    public static Response doGetAllResponse() {
        return given()
                .when()
                .get();
    }

    public static Response doGetResponse(Long passengerId) {
        return given()
                .when()
                .get("/" + passengerId);
    }

    public static Response doPostResponse(PassengerRequest requestBody) {
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post();
    }

    public static Response doPutResponse(PassengerRequest requestBody, Long passengerId) {
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/" + passengerId);
    }

    public static Response doDeleteResponse(Long passengerId) {
        return given()
                .when()
                .delete("/" + passengerId);
    }
}
