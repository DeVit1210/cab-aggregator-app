package com.modsen.payment.dto.response;

import java.util.List;

public record PaymentListResponse(
        List<PaymentResponse> payments,
        int quantity
) {
    public static PaymentListResponse of(List<PaymentResponse> payments) {
        return new PaymentListResponse(payments, payments.size());
    }
}
