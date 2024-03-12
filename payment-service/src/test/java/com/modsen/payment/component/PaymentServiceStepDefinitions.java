package com.modsen.payment.component;

import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.PaymentListResponse;
import com.modsen.payment.dto.response.PaymentResponse;
import com.modsen.payment.mapper.PaymentMapperImpl;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.model.Payment;
import com.modsen.payment.repository.PaymentRepository;
import com.modsen.payment.service.CreditCardService;
import com.modsen.payment.service.DriverAccountService;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.service.impl.PaymentServiceImpl;
import com.modsen.payment.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentServiceStepDefinitions {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapperImpl paymentMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private StripeCustomerService stripeCustomerService;
    @Mock
    private CreditCardService creditCardService;
    @Mock
    private DriverAccountService driverAccountService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private PaymentListResponse paymentListResponse;


    public PaymentServiceStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Valid payment with amount of {double} request and ride has not been paid yet")
    public void validPaymentRequestAndRideHasNotBeenPaidYet(double paymentAmount) {
        payment = TestUtils.paymentWithAmount(
                BigDecimal.valueOf(paymentAmount)
        );
        paymentRequest = TestUtils.paymentRequestWithAmount(
                BigDecimal.valueOf(paymentAmount)
        );
    }

    @When("Passenger pays for the ride")
    public void businessLogicToCreatePaymentIsInvoked() {
        CreditCard creditCard = TestUtils.defaultCreditCard();

        when(paymentMapper.toPayment(any(PaymentRequest.class)))
                .thenReturn(payment);
        when(stripeCustomerService.getCustomerId(anyLong()))
                .thenReturn(TestConstants.Stripe.CUSTOMER_ID);
        when(stripeService.createPayment(anyString(), any(BigDecimal.class)))
                .thenReturn(TestConstants.Stripe.CREDIT_CARD_ID);
        when(creditCardService.findCreditCardByStripeId(anyString()))
                .thenReturn(creditCard);
        doNothing().when(driverAccountService)
                .replenishAccount(anyLong(), any(BigDecimal.class));
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);
        when(paymentMapper.toPaymentResponse(any(Payment.class)))
                .thenCallRealMethod();

        paymentResponse = paymentService.createPayment(paymentRequest);
    }

    @Then("Payment response should be present and contain created payment with amount of {double}")
    public void paymentResponseShouldBePresentAndContainCreatedPayment(double expectedPaymentAmount) {
        assertThat(paymentResponse)
                .isNotNull()
                .extracting(PaymentResponse::amount)
                .isEqualTo(BigDecimal.valueOf(expectedPaymentAmount));
    }

    @And("Methods needed to create payment were called")
    public void methodsNeededToCreatePaymentWereCalled() {
        verify(paymentMapper).toPayment(paymentRequest);
        verify(stripeCustomerService).getCustomerId(paymentRequest.getPassengerId());
        verify(stripeService).createPayment(anyString(), any());
        verify(creditCardService).findCreditCardByStripeId(anyString());
        verify(driverAccountService).replenishAccount(any(), any());
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Given("Ride with id {long} has already been paid")
    public void rideWithIdHasAlreadyBeenPaid(long rideId) {
        payment = TestUtils.defaultPayment();
        payment.setRideId(rideId);

        when(paymentRepository.findByRideId(rideId))
                .thenReturn(Optional.of(payment));
    }

    @When("Passenger search for a payment for a ride with id {long}")
    public void businessLogicToRetrievePaymentForARideIsInvoked(long rideId) {
        when(paymentMapper.toPaymentResponse(any(Payment.class)))
                .thenCallRealMethod();

        paymentResponse = paymentService.findPaymentByRide(rideId);
    }

    @Then("Payment response should be present and contain payment for a ride with id {long}")
    public void paymentResponseShouldBePresentAndContainPaymentForARideWithId(long expectedRideId) {
        assertThat(paymentResponse)
                .isNotNull()
                .extracting(PaymentResponse::rideId)
                .isEqualTo(expectedRideId);
    }

    @And("Methods needed to get payment for a ride were called")
    public void methodsNeededToGetPaymentForARideWereCalled() {
        verify(paymentRepository).findByRideId(anyLong());
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Given("{int} payments exist for passenger")
    public void paymentsExistForPassenger(int paymentsQuantity) {
        List<Payment> paymentList = Collections.nCopies(paymentsQuantity, TestUtils.defaultPayment());

        when(paymentRepository.findAllByPassengerId(anyLong()))
                .thenReturn(paymentList);
    }

    @When("Passenger search for all his payments")
    public void businessLogicToRetrieveAllPaymentsForAPassengerIsInvoked() {
        when(paymentMapper.toPaymentResponseList(anyList()))
                .thenCallRealMethod();

        paymentListResponse = paymentService.findAllPaymentsByPassenger(TestConstants.PASSENGER_ID);
    }

    @Then("Payment response should contain {int} payments")
    public void paymentResponseShouldContainPayments(int expectedPaymentsQuantity) {
        assertThat(paymentListResponse.payments())
                .hasSize(expectedPaymentsQuantity);
    }

    @And("Methods needed to get all payments for a passenger were called")
    public void methodsNeededToGetAllPaymentsForAPassengerWereCalled() {
        verify(paymentRepository).findAllByPassengerId(TestConstants.PASSENGER_ID);
        verify(paymentMapper).toPaymentResponseList(anyList());
    }
}
