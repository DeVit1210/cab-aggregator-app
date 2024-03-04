package com.modsen.payment.integration;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.PaymentResponse;
import com.modsen.payment.exception.ApiExceptionInfo;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.model.Payment;
import com.modsen.payment.model.StripeCustomer;
import com.modsen.payment.repository.PaymentRepository;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.utils.RestAssuredUtils;
import com.modsen.payment.utils.TestUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "classpath:insert-stripe-customers-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:insert-credit-cards-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:insert-driver-accounts-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class PaymentControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private PaymentRepository paymentRepository;
    @MockBean
    private StripeService stripeService;

    static Stream<Arguments> findPaymentsByPassengerArgumentsProvider() {
        return Stream.of(
                Arguments.of(1L, 2),
                Arguments.of(2L, 1),
                Arguments.of(3L, 0)
        );
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
    }

    @Test
    @Sql("classpath:insert-payments-data.sql")
    void findPaymentByRide_PaymentExists_ShouldReturnPayment() {
        Payment expectedPayment = TestUtils.defaultPayment();
        Long rideId = expectedPayment.getRideId();

        PaymentResponse paymentResponse = RestAssuredUtils.findPaymentByRideResponse(rideId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.RIDE_ID_FIELD, equalTo(rideId.intValue()))
                .extract()
                .as(PaymentResponse.class);

        Optional<Payment> payment = paymentRepository.findById(paymentResponse.id());
        assertTrue(payment.isPresent());
    }

    @Test
    void findPaymentByRide_PaymentDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long rideId = TestConstants.RIDE_ID;
        String expectedExceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                Payment.class.getSimpleName(),
                rideId
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response findPaymentByRideResponse = RestAssuredUtils.findPaymentByRideResponse(rideId);

        extractApiExceptionInfoAndAssert(findPaymentByRideResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @ParameterizedTest
    @MethodSource("findPaymentsByPassengerArgumentsProvider")
    @Sql("classpath:insert-payments-data.sql")
    void findPaymentsByPassenger_Success(Long passengerId, int expectedPaymentsQuantity) {
        RestAssuredUtils.findAllPaymentsByPassengerResponse(passengerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.QUANTITY_FIELD, equalTo(expectedPaymentsQuantity));
    }

    @Test
    void createPayment_ValidPaymentRequest_ShouldReturnCreatedPayment() {
        BigDecimal paymentAmount = BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP);
        PaymentRequest paymentRequest = TestUtils.paymentRequestWithAmount(paymentAmount);

        Mockito.when(stripeService.createPayment(anyString(), any()))
                .thenReturn(TestConstants.Stripe.CREDIT_CARD_ID);

        PaymentResponse paymentResponse = RestAssuredUtils.createPaymentResponse(paymentRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body(TestConstants.FieldNames.RIDE_ID_FIELD, equalTo(paymentRequest.getRideId().intValue()))
                .body(TestConstants.FieldNames.PASSENGER_ID_FIELD, equalTo(paymentRequest.getPassengerId().intValue()))
                .body(TestConstants.FieldNames.AMOUNT_FIELD, equalTo(paymentRequest.getAmount().floatValue()))
                .extract()
                .as(PaymentResponse.class);

        Optional<Payment> createdPayment = paymentRepository.findById(paymentResponse.id());
        assertTrue(createdPayment.isPresent());
    }

    @Test
    @Sql("classpath:insert-payments-data.sql")
    void createPayment_RideHasAlreadyBeenPaid_ShouldReturnApiExceptionInfo() {
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        ApiExceptionInfo apiExceptionInfo = RestAssuredUtils.createPaymentResponse(paymentRequest)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }

    @Test
    void createPayment_StripeCustomerDoesNotExist_ShouldReturnApiExceptionInfo() {
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();
        Long nonExistentStripeCustomerId = 100L;
        paymentRequest.setPassengerId(nonExistentStripeCustomerId);
        String expectedExceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                StripeCustomer.class.getSimpleName(),
                paymentRequest.getPassengerId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response createPaymentResponse = RestAssuredUtils.createPaymentResponse(paymentRequest);

        extractApiExceptionInfoAndAssert(createPaymentResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void createPayment_NoDefaultCardForPassenger_ShouldReturnApiExceptionInfo() {
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_STRIPE_ID.getValue(),
                CreditCard.class.getSimpleName(),
                null
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response createPaymentResponse = RestAssuredUtils.createPaymentResponse(paymentRequest);

        extractApiExceptionInfoAndAssert(createPaymentResponse, expectedHttpStatus, exceptionMessage);
    }
}
