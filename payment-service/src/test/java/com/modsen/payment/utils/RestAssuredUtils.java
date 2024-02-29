package com.modsen.payment.utils;

import com.modsen.payment.constants.ControllerMappings;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.enums.Role;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.given;

@UtilityClass
public class RestAssuredUtils {
    public static Response createCreditCardResponse(CreditCardRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ControllerMappings.CREDIT_CARD_CONTROLLER);
    }

    public static Response findAllByIdAndRoleResponse(Long cardHolderId, Role role) {
        return given()
                .param(TestConstants.FieldNames.ROLE_FIELD, role.name())
                .param(TestConstants.FieldNames.CARD_HOLDER_ID_FIELD, cardHolderId)
                .when()
                .get(ControllerMappings.CREDIT_CARD_CONTROLLER);
    }

    public static Response findCreditCardByIdResponse(Long creditCardId) {
        return given()
                .when()
                .get(ControllerMappings.CREDIT_CARD_CONTROLLER + "/" + creditCardId);
    }

    public static Response changeDefaultCardResponse(Long creditCardId) {
        return given()
                .when()
                .put(ControllerMappings.CREDIT_CARD_CONTROLLER + "/default/" + creditCardId);
    }

    public static Response createPaymentResponse(PaymentRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ControllerMappings.PAYMENT_CONTROLLER);
    }

    public static Response findPaymentByRideResponse(Long rideId) {
        return given()
                .when()
                .get(ControllerMappings.PAYMENT_CONTROLLER + "/ride/" + rideId);
    }

    public static Response findAllPaymentsByPassengerResponse(Long passengerId) {
        return given()
                .when()
                .get(ControllerMappings.PAYMENT_CONTROLLER + "/passenger/" + passengerId);
    }

    public static Response createPayoutResponse(DriverPayoutRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ControllerMappings.DRIVER_PAYOUT_CONTROLLER);
    }

    public static Response findAllPayoutsForDriverResponse(Long driverId) {
        return given()
                .when()
                .get(ControllerMappings.DRIVER_PAYOUT_CONTROLLER + "/" + driverId);
    }
}
