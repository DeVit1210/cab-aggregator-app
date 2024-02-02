package com.modsen.payment.service.impl;

import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.dto.response.PaymentListResponse;
import com.modsen.payment.dto.response.PaymentResponse;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.mapper.PaymentMapper;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.model.Payment;
import com.modsen.payment.repository.PaymentRepository;
import com.modsen.payment.service.CreditCardService;
import com.modsen.payment.service.DriverAccountService;
import com.modsen.payment.service.PaymentService;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final StripeCustomerService stripeCustomerService;
    private final CreditCardService creditCardService;
    private final DriverAccountService driverAccountService;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = paymentMapper.toPayment(request);

        String stripeCustomerId = stripeCustomerService.getCustomerId(request.getPassengerId());
        String paymentCreditCardStripeId = stripeService.createPayment(stripeCustomerId, request.getAmount());
        CreditCard creditCard = creditCardService.findCreditCardByStripeId(paymentCreditCardStripeId);
        payment.setCreditCardId(creditCard.getId());

        BigDecimal driverIncomeForRide = getDriverAmount(request.getAmount());
        driverAccountService.replenishAccount(request.getDriverId(), driverIncomeForRide);

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    @Override
    public PaymentResponse findPaymentByRide(Long rideId) {
        return paymentRepository.findByRideId(rideId)
                .map(paymentMapper::toPaymentResponse)
                .orElseThrow(() -> new PaymentEntityNotFoundException(rideId, Payment.class));
    }

    @Override
    public PaymentListResponse findAllPaymentsByPassenger(Long passengerId) {
        List<Payment> paymentList = paymentRepository.findAllByPassengerId(passengerId);
        List<PaymentResponse> paymentResponseList = paymentMapper.toPaymentResponseList(paymentList);

        return PaymentListResponse.of(paymentResponseList);
    }

    @Override
    public Paged<PaymentResponse> findAllPayments(PageSettingsRequest request) {
        PageRequest pageRequest = PageRequestUtils.pageRequestForEntity(request, Payment.class);
        Page<Payment> paymentPage = paymentRepository.findAll(pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, paymentPage);

        return paymentMapper.toPagedPaymentResponse(paymentPage);
    }

    private BigDecimal getDriverAmount(BigDecimal totalRideCost) {
        return totalRideCost.multiply(BigDecimal.valueOf(0.2))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
