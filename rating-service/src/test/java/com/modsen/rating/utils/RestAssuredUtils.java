package com.modsen.rating.utils;

import com.modsen.rating.constants.TestConstants;
import com.modsen.rating.dto.request.RatingRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.given;

@UtilityClass
public class RestAssuredUtils {
    public static Response getAllRatingsForPersonResponse(Long ratedPersonId, String role) {
        return given()
                .param(TestConstants.FieldNames.RATED_PERSON_ID_FIELD, ratedPersonId)
                .param(TestConstants.FieldNames.ROLE_FIELD, role)
                .when()
                .get();
    }

    public static Response getRatingResponse(Long ratingId) {
        return given()
                .when()
                .get("/" + ratingId);
    }

    public static Response getAverageRatingResponse(Long ratedPersonId, String role) {
        return given()
                .param(TestConstants.FieldNames.ROLE_FIELD, role)
                .when()
                .get(TestConstants.Url.AVERAGE_RATING_PATH + "/" + ratedPersonId);
    }

    public static Response getAllAverageRatingsResponse(String role) {
        return given()
                .param(TestConstants.FieldNames.ROLE_FIELD, role)
                .when()
                .get(TestConstants.Url.AVERAGE_RATING_PATH);
    }

    public static Response createRatingResponse(RatingRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post();
    }

    public static Response updateRatingResponse(Long ratingId, String ratingValue) {
        return given()
                .param(TestConstants.FieldNames.RATING_VALUE_FIELD, ratingValue)
                .when()
                .patch("/" + ratingId);
    }
}
