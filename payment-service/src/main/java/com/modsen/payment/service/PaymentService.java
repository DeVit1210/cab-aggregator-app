package com.modsen.payment.service;

import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.dto.response.PaymentListResponse;
import com.modsen.payment.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse findPaymentByRide(Long rideId);

    PaymentListResponse findAllPaymentsByPassenger(Long passengerId);

    Paged<PaymentResponse> findAllPayments(PageSettingsRequest request);
}
