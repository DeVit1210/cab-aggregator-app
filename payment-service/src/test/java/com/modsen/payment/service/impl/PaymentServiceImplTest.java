package com.modsen.payment.service.impl;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.PaymentListResponse;
import com.modsen.payment.dto.response.PaymentResponse;
import com.modsen.payment.exception.CustomStripeException;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.exception.RideAlreadyPaidException;
import com.modsen.payment.mapper.PaymentMapperImpl;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.model.Payment;
import com.modsen.payment.repository.PaymentRepository;
import com.modsen.payment.service.CreditCardService;
import com.modsen.payment.service.DriverAccountService;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
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

    @BeforeAll
    static void beforeAll() {
        mockStatic(LocalDateTime.class).when(LocalDateTime::now)
                .thenReturn(CURRENT_TIME);
    }

    @Test
    void findPaymentByRide_ValidRideId_ShouldReturnPayment() {
        Long rideId = TestConstants.RIDE_ID;
        Payment payment = TestUtils.defaultPayment();

        when(paymentRepository.findByRideId(rideId))
                .thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentResponse(any(Payment.class)))
                .thenCallRealMethod();

        PaymentResponse actualPayment = paymentService.findPaymentByRide(rideId);

        assertNotNull(actualPayment);
        verify(paymentRepository).findByRideId(rideId);
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Test
    void findPaymentByRide_InvalidRideId_ThrowPaymentEntityNotFoundException() {
        Long rideId = TestConstants.RIDE_ID;
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                Payment.class.getSimpleName(),
                rideId
        );

        when(paymentRepository.findByRideId(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findPaymentByRide(rideId))
                .isInstanceOf(PaymentEntityNotFoundException.class)
                .hasMessage(exceptionMessage);
        verify(paymentRepository).findByRideId(rideId);
        verify(paymentMapper, never()).toPaymentResponse(any(Payment.class));
    }

    @Test
    void findAllPaymentsByPassenger_AtLeastOnePaymentExists_ShouldReturnPayments() {
        Long passengerId = TestConstants.PASSENGER_ID;
        List<Payment> paymentList = Collections.nCopies(3, TestUtils.defaultPayment());

        when(paymentRepository.findAllByPassengerId(anyLong()))
                .thenReturn(paymentList);
        when(paymentMapper.toPaymentResponseList(anyList()))
                .thenCallRealMethod();

        PaymentListResponse actualPaymentList = paymentService.findAllPaymentsByPassenger(passengerId);

        assertEquals(paymentList.size(), actualPaymentList.quantity());
        verify(paymentRepository).findAllByPassengerId(passengerId);
        verify(paymentMapper).toPaymentResponseList(paymentList);
    }

    @Test
    void findAllPaymentsByPassenger_NoPaymentsExists_ShouldReturnEmptyList() {
        Long passengerId = TestConstants.PASSENGER_ID;

        when(paymentRepository.findAllByPassengerId(anyLong()))
                .thenReturn(Collections.emptyList());

        PaymentListResponse actualPaymentList = paymentService.findAllPaymentsByPassenger(passengerId);

        assertTrue(actualPaymentList.payments().isEmpty());
        verify(paymentRepository).findAllByPassengerId(passengerId);
        verify(paymentMapper).toPaymentResponseList(Collections.emptyList());
    }

    @Test
    void createPayment_ValidPaymentRequest_ShouldReturnCreatedPayment() {
        String stripeCustomerId = TestConstants.Stripe.CUSTOMER_ID;
        String stripeCardId = TestConstants.Stripe.CREDIT_CARD_ID;
        BigDecimal paymentAmount = BigDecimal.TEN;
        BigDecimal driverAmountForRide = paymentAmount.multiply(BigDecimal.valueOf(0.2))
                .setScale(2, RoundingMode.HALF_UP);
        Payment payment = TestUtils.defaultPayment();
        PaymentRequest paymentRequest = TestUtils.paymentRequestWithAmount(paymentAmount);
        CreditCard creditCard = TestUtils.defaultCreditCard();

        when(paymentMapper.toPayment(any(PaymentRequest.class)))
                .thenReturn(payment);
        when(stripeCustomerService.getCustomerId(anyLong()))
                .thenReturn(stripeCustomerId);
        when(stripeService.createPayment(anyString(), any(BigDecimal.class)))
                .thenReturn(stripeCardId);
        when(creditCardService.findCreditCardByStripeId(anyString()))
                .thenReturn(creditCard);
        doNothing().when(driverAccountService)
                .replenishAccount(anyLong(), any(BigDecimal.class));
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);
        when(paymentMapper.toPaymentResponse(any(Payment.class)))
                .thenCallRealMethod();

        PaymentResponse actualPayment = paymentService.createPayment(paymentRequest);

        assertNotNull(actualPayment);
        assertEquals(creditCard.getId(), payment.getCreditCardId());
        verify(paymentMapper).toPayment(paymentRequest);
        verify(stripeCustomerService).getCustomerId(paymentRequest.getPassengerId());
        verify(stripeService).createPayment(stripeCustomerId, paymentAmount);
        verify(creditCardService).findCreditCardByStripeId(stripeCardId);
        verify(driverAccountService).replenishAccount(paymentRequest.getDriverId(), driverAmountForRide);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Test
    void createPayment_PaymentAlreadyExists_ThrowRideAlreadyPaidException() {
        Long rideId = TestConstants.RIDE_ID;
        Payment payment = TestUtils.defaultPayment();
        payment.setCreatedAt(CURRENT_TIME);
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();
        String exceptionMessage = String.format(
                MessageTemplates.RIDE_ALREADY_PAID.getValue(),
                rideId,
                CURRENT_TIME
        );

        when(paymentRepository.findByRideId(anyLong()))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest))
                .isInstanceOf(RideAlreadyPaidException.class)
                .hasMessage(exceptionMessage);
        verify(stripeService, never()).createPayment(anyString(), any());
        verify(driverAccountService, never()).replenishAccount(anyLong(), any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_StripeCustomerNotFound_ThrowPaymentEntityNotFoundException() {
        Payment payment = TestUtils.defaultPayment();
        payment.setCreatedAt(CURRENT_TIME);
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();

        when(paymentMapper.toPayment(any(PaymentRequest.class)))
                .thenReturn(payment);
        when(stripeCustomerService.getCustomerId(anyLong()))
                .thenThrow(PaymentEntityNotFoundException.class);

        assertThrowsExactly(PaymentEntityNotFoundException.class, () -> paymentService.createPayment(paymentRequest));
        verify(stripeCustomerService).getCustomerId(TestConstants.PASSENGER_ID);
        verify(stripeService, never()).createPayment(anyString(), any());
        verify(driverAccountService, never()).replenishAccount(anyLong(), any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_CreditCardNotFound_ThrowCustomStripeException() {
        String stripeCustomerId = TestConstants.Stripe.CUSTOMER_ID;
        Payment payment = TestUtils.defaultPayment();
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();

        when(paymentMapper.toPayment(any(PaymentRequest.class)))
                .thenReturn(payment);
        when(stripeCustomerService.getCustomerId(anyLong()))
                .thenReturn(stripeCustomerId);
        when(stripeService.createPayment(anyString(), any(BigDecimal.class)))
                .thenThrow(CustomStripeException.class);

        assertThrowsExactly(CustomStripeException.class, () -> paymentService.createPayment(paymentRequest));
        verify(paymentMapper).toPayment(paymentRequest);
        verify(stripeCustomerService).getCustomerId(paymentRequest.getPassengerId());
        verify(stripeService).createPayment(stripeCustomerId, paymentRequest.getAmount());
        verify(driverAccountService, never()).replenishAccount(anyLong(), any());
        verify(paymentRepository, never()).save(any());
    }

}