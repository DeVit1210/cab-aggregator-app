package com.modsen.ride.utils;

import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.request.RideCostRequest;
import com.modsen.ride.dto.request.RideRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.given;

@UtilityClass
public class RestAssuredUtils {
    public static Response createRideResponse(RideRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ServiceMappings.RIDE_CONTROLLER);
    }

    public static Response findRideByIdResponse(Long rideId) {
        return given()
                .when()
                .get(ServiceMappings.RIDE_CONTROLLER + "/" + rideId);
    }

    public static Response findAllRidesForPerson(Long personId, String role) {
        return given()
                .param(TestConstants.FieldNames.ROLE_FIELD, role)
                .param(TestConstants.FieldNames.PERSON_ID_FIELD, personId)
                .when()
                .get(ServiceMappings.RIDE_CONTROLLER);
    }

    public static Response findAvailableRideForDriver(Long driverId) {
        return given()
                .when()
                .get(ServiceMappings.RIDE_CONTROLLER + TestConstants.Url.AVAILABLE_DRIVER_RIDE + driverId);
    }

    public static Response findConfirmedRideForPassenger(Long passengerId) {
        return given()
                .when()
                .get(ServiceMappings.RIDE_CONTROLLER + TestConstants.Url.CONFIRMED_PASSENGER_RIDE + passengerId);
    }

    public static Response calculateRideCostResponse(RideCostRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ServiceMappings.RIDE_COST_CONTROLLER);
    }

    public static Response acceptRideResponse(Long rideId) {
        return rideOperationsResponse(TestConstants.Url.ACCEPT_RIDE, rideId);
    }

    public static Response dismissRideResponse(Long rideId) {
        return rideOperationsResponse(TestConstants.Url.DISMISS_RIDE, rideId);
    }

    public static Response notifyPassengerResponse(Long rideId) {
        return rideOperationsResponse(TestConstants.Url.NOTIFY_PASSENGER, rideId);
    }

    public static Response startRideResponse(Long rideId) {
        return rideOperationsResponse(TestConstants.Url.START_RIDE, rideId);
    }

    public static Response cancelRideResponse(Long rideId) {
        return rideOperationsResponse(TestConstants.Url.CANCEL_RIDE, rideId);
    }

    public static Response finishRideResponse(FinishRideRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .patch(ServiceMappings.RIDE_OPERATIONS_CONTROLLER + TestConstants.Url.FINISH_RIDE);
    }

    private static Response rideOperationsResponse(String urlPrefix, Long rideId) {
        return given()
                .when()
                .patch(ServiceMappings.RIDE_OPERATIONS_CONTROLLER + urlPrefix + "/" + rideId);
    }
}
