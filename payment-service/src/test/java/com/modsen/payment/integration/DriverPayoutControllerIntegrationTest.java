package com.modsen.payment.integration;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.model.DriverPayout;
import com.modsen.payment.repository.DriverPayoutRepository;
import com.modsen.payment.utils.RestAssuredUtils;
import com.modsen.payment.utils.TestUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "classpath:insert-driver-accounts-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:insert-credit-cards-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class DriverPayoutControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private DriverPayoutRepository driverPayoutRepository;

    @AfterEach
    void tearDown() {
        driverPayoutRepository.deleteAll();
    }

    @Test
    @Sql("classpath:insert-driver-payouts-data.sql")
    void findAllPayoutsForDriver_AtLeastOnePayoutExist_ShouldReturnPayoutsList() {
        Long driverId = TestConstants.DRIVER_ID;
        int expectedPayoutsQuantity = 2;

        RestAssuredUtils.findAllPayoutsForDriverResponse(driverId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.PAYOUT_LIST_FIELD, iterableWithSize(expectedPayoutsQuantity));
    }

    @Test
    void findAllPayoutsForDriver_NoExistingPayouts_ShouldReturnEmptyList() {
        RestAssuredUtils.findAllPayoutsForDriverResponse(TestConstants.DRIVER_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.PAYOUT_LIST_FIELD, emptyIterable());
    }

    @Test
    void createDriverPayout_ValidPayoutRequest_ShouldReturnCreatedPayout() {
        BigDecimal payoutAmount = BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP);
        DriverPayoutRequest payoutRequest = TestUtils.driverPayoutRequestWithAmount(payoutAmount);
        DriverPayout expectedPayout = TestUtils.driverPayout(payoutAmount);

        DriverPayoutResponse driverPayoutResponse = RestAssuredUtils.createPayoutResponse(payoutRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(DriverPayoutResponse.class);

        Optional<DriverPayout> driverPayout = driverPayoutRepository.findById(driverPayoutResponse.id());

        assertTrue(driverPayout.isPresent());
        assertEquals(expectedPayout.getWithdrawAmount(), driverPayout.get().getWithdrawAmount());
        assertEquals(expectedPayout.getCreditCardId(), driverPayout.get().getCreditCardId());
    }

    @Test
    void createDriverPayout_CardDoesNotBelongToAnyDriver_ShouldReturnApiExceptionInfo() {
        Long invalidCreditCardId = 3L;
        DriverPayoutRequest payoutRequest = TestUtils.driverPayoutRequestWithAmount(BigDecimal.ONE);
        payoutRequest.setCreditCardId(invalidCreditCardId);
        String expectedExceptionMessage = String.format(
                MessageTemplates.CREDIT_CARD_INVALID_HOLDER.getValue(),
                payoutRequest.getCreditCardId(),
                Role.DRIVER.name(),
                payoutRequest.getDriverId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response createPayoutResponse = RestAssuredUtils.createPayoutResponse(payoutRequest);

        extractApiExceptionInfoAndAssert(createPayoutResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void createDriverPayout_DriverAccountDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long nonExistentDriverAccountId = 100L;
        DriverPayoutRequest payoutRequest = TestUtils.driverPayoutRequestWithAmount(BigDecimal.TEN);
        payoutRequest.setDriverId(nonExistentDriverAccountId);
        String expectedExceptionMessage = String.format(
                MessageTemplates.CREDIT_CARD_INVALID_HOLDER.getValue(),
                payoutRequest.getCreditCardId(),
                Role.DRIVER.name(),
                payoutRequest.getDriverId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response createPayoutResponse = RestAssuredUtils.createPayoutResponse(payoutRequest);

        extractApiExceptionInfoAndAssert(createPayoutResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void createDriverAccount_PayoutAmountHigherThanActualBalance_ShouldReturnApiExceptionInfo() {
        BigDecimal payoutAmount = BigDecimal.valueOf(100);
        DriverPayoutRequest payoutRequest = TestUtils.driverPayoutRequestWithAmount(payoutAmount);
        String expectedExceptionMessage = String.format(
                MessageTemplates.INCUFFICIENT_ACCOUNT_BALANCE.getValue(),
                payoutAmount
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response createPayoutResponse = RestAssuredUtils.createPayoutResponse(payoutRequest);

        extractApiExceptionInfoAndAssert(createPayoutResponse, expectedHttpStatus, expectedExceptionMessage);
    }
}
