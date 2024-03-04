package com.modsen.promocode.integration;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.constants.TestConstants;
import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.enums.ApplianceStatus;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.repository.AppliedPromocodeRepository;
import com.modsen.promocode.service.feign.RideServiceClient;
import com.modsen.promocode.utils.RestAssuredUtils;
import com.modsen.promocode.utils.TestUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "classpath:insert-promocodes-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class AppliedPromocodeControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private AppliedPromocodeRepository appliedPromocodeRepository;
    @MockBean
    private RideServiceClient rideServiceClient;

    @AfterEach
    void tearDown() {
        appliedPromocodeRepository.deleteAll();
    }

    @Test
    void applyPromocode_ValidApplyPromocodeRequest_ShouldReturnAppliedPromocode() {
        ApplyPromocodeRequest applyPromocodeRequest = TestUtils.defaultApplyPromocodeRequest();
        AppliedPromocode expectedAppliedPromocode = TestUtils.defaultAppliedPromocode();

        Mockito.when(rideServiceClient.findAllRidesForPerson(anyLong(), any()))
                .thenReturn(TestUtils.emptyRideListResponse());

        AppliedPromocodeResponse appliedPromocodeResponse = RestAssuredUtils.applyPromocodeResponse(applyPromocodeRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(AppliedPromocodeResponse.class);

        Optional<AppliedPromocode> appliedPromocode = appliedPromocodeRepository.findById(appliedPromocodeResponse.id());

        assertTrue(appliedPromocode.isPresent());
        assertEquals(expectedAppliedPromocode.getPromocode().getId(), appliedPromocodeResponse.promocodeId());
        assertEquals(expectedAppliedPromocode.getPassengerId(), appliedPromocode.get().getPassengerId());
        assertEquals(ApplianceStatus.NOT_CONFIRMED, appliedPromocode.get().getApplianceStatus());
    }

    @Test
    void applyPromocode_PromocodeDoesNotExist_ShouldReturnApiExceptionInfo() {
        String invalidPromocodeName = TestConstants.INVALID_PROMOCODE_NAME;
        ApplyPromocodeRequest applyPromocodeRequest = TestUtils.applyPromocodeRequestWithName(invalidPromocodeName);
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_NOT_FOUND_BY_NAME.getValue(),
                applyPromocodeRequest.getPromocodeName()
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response applyPromocodeResponse = RestAssuredUtils.applyPromocodeResponse(applyPromocodeRequest);

        extractApiExceptionInfoAndAssert(applyPromocodeResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-applied-promocodes-data.sql")
    void applyPromocode_PromocodeAlreadyApplied_ShouldReturnApiExceptionInfo() {
        ApplyPromocodeRequest applyPromocodeRequest = TestUtils.defaultApplyPromocodeRequest();
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_ALREADY_APPLIED.getValue(),
                applyPromocodeRequest.getPromocodeName(),
                applyPromocodeRequest.getPassengerId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        Response applyPromocodeResponse = RestAssuredUtils.applyPromocodeResponse(applyPromocodeRequest);

        extractApiExceptionInfoAndAssert(applyPromocodeResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void applyPromocode_InsufficientRideAmountToApply_ShouldReturnApiExceptionInfo() {
        ApplyPromocodeRequest applyPromocodeRequest = TestUtils.applyPromocodeRequestWithName("PROMO20");
        String expectedExceptionMessage = String.format(
                MessageTemplates.INVALID_RIDE_AMOUNT_FOR_PROMOCODE.getValue(),
                applyPromocodeRequest.getPromocodeName(),
                100,
                0
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Mockito.when(rideServiceClient.findAllRidesForPerson(anyLong(), any()))
                .thenReturn(TestUtils.emptyRideListResponse());

        Response applyPromocodeResponse = RestAssuredUtils.applyPromocodeResponse(applyPromocodeRequest);

        extractApiExceptionInfoAndAssert(applyPromocodeResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-applied-promocodes-data.sql")
    void findNotConfirmedPromocode_PromocodeExists_ShouldReturnNotConfirmedPromocode() {
        Long passengerId = TestConstants.PASSENGER_ID;

        RestAssuredUtils.findNotConfirmedPromocodeResponse(passengerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.PROMOCODE_ID_FIELD, equalTo(TestConstants.PROMOCODE_ID.intValue()))
                .body(TestConstants.FieldNames.PASSENGER_ID_FIELD, equalTo(passengerId.intValue()));
    }

    @Test
    @Sql("classpath:insert-applied-promocodes-data.sql")
    void findNotConfirmedPromocode_PromocodeDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long invalidPassengerId = 100L;
        String expectedExceptionMessage = String.format(
                MessageTemplates.NO_PROMOCODE_FOR_PASSENGER.getValue(),
                invalidPassengerId
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response findNotConfirmedPromocodeResponse =
                RestAssuredUtils.findNotConfirmedPromocodeResponse(invalidPassengerId);

        extractApiExceptionInfoAndAssert(findNotConfirmedPromocodeResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-applied-promocodes-data.sql")
    void confirmPromocodeAppliance_ValidPromocodeId_ShouldReturnConfirmedPromocode() {
        Long promocodeId = TestConstants.PROMOCODE_ID;

        AppliedPromocodeResponse appliedPromocode = RestAssuredUtils.confirmPromocodeApplianceResponse(promocodeId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AppliedPromocodeResponse.class);

        Optional<AppliedPromocode> confirmedPromocode = appliedPromocodeRepository.findById(appliedPromocode.id());

        assertTrue(confirmedPromocode.isPresent());
        assertEquals(promocodeId, confirmedPromocode.get().getId());
        assertEquals(ApplianceStatus.CONFIRMED, confirmedPromocode.get().getApplianceStatus());
    }

    @Test
    @Sql("classpath:insert-applied-promocodes-data.sql")
    void confirmPromocodeAppliance_PromocodeAlreadyConfirmed_ShouldReturnApiExceptionInfo() {
        Long invalidPromocodeId = 2L;
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_ALREADY_APPLIED.getValue(),
                "PROMO20",
                1
        );
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        Response confirmPromocodeApplianceResponse =
                RestAssuredUtils.confirmPromocodeApplianceResponse(invalidPromocodeId);

        extractApiExceptionInfoAndAssert(confirmPromocodeApplianceResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-applied-promocodes-data.sql")
    void confirmPromocodeAppliance_PromocodeDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long invalidPromocodeId = 100L;
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(),
                invalidPromocodeId
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response confirmPromocodeApplianceResponse =
                RestAssuredUtils.confirmPromocodeApplianceResponse(invalidPromocodeId);

        extractApiExceptionInfoAndAssert(confirmPromocodeApplianceResponse, expectedHttpStatus, expectedExceptionMessage);
    }
}
