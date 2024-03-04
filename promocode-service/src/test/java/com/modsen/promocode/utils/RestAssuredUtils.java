package com.modsen.promocode.utils;

import com.modsen.promocode.constants.ServiceMappings;
import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.given;

@UtilityClass
public class RestAssuredUtils {
    public static Response findAllPromocodesResponse() {
        return given()
                .when()
                .get(ServiceMappings.PROMOCODE_CONTROLLER);
    }

    public static Response findPromocodeByIdResponse(Long promocodeId) {
        return given()
                .when()
                .get(ServiceMappings.PROMOCODE_CONTROLLER + "/" + promocodeId);
    }

    public static Response createPromocodeResponse(PromocodeRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ServiceMappings.PROMOCODE_CONTROLLER);
    }

    public static Response updatePromocodeDiscountPercentResponse(UpdateDiscountPercentRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch(ServiceMappings.PROMOCODE_CONTROLLER);
    }

    public static Response deletePromocodeResponse(Long promocodeId) {
        return given()
                .when()
                .delete(ServiceMappings.PROMOCODE_CONTROLLER + "/" + promocodeId);
    }

    public static Response applyPromocodeResponse(ApplyPromocodeRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ServiceMappings.APPLIED_PROMOCODE_CONTROLLER);
    }

    public static Response findNotConfirmedPromocodeResponse(Long passengerId) {
        return given()
                .when()
                .get(ServiceMappings.APPLIED_PROMOCODE_CONTROLLER + "/" + passengerId);
    }

    public static Response confirmPromocodeApplianceResponse(Long promocodeId) {
        return given()
                .when()
                .patch(ServiceMappings.APPLIED_PROMOCODE_CONTROLLER + "/" + promocodeId);
    }
}
