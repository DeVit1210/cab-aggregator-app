package com.modsen.payment.controller;

import com.modsen.payment.constants.ControllerMappings;
import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.dto.response.PaymentListResponse;
import com.modsen.payment.dto.response.PaymentResponse;
import com.modsen.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerMappings.PAYMENT_CONTROLLER)
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@RequestBody @Valid PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/ride/{rideId}")
    public PaymentResponse findPaymentByRid(@PathVariable Long rideId) {
        return paymentService.findPaymentByRide(rideId);
    }

    @GetMapping("/passenger/{passengerId}")
    public PaymentListResponse findAllPaymentsByPassenger(@PathVariable Long passengerId) {
        return paymentService.findAllPaymentsByPassenger(passengerId);
    }

    @GetMapping("/page")
    public Paged<PaymentResponse> findPayments(PageSettingsRequest request) {
        return paymentService.findAllPayments(request);
    }
}
