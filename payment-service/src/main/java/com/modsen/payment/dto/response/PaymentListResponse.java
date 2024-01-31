package com.modsen.payment.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentListResponse {
    private List<PaymentResponse> payments;
    private int quantity;

    public static PaymentListResponse of(List<PaymentResponse> payments) {
        return new PaymentListResponse(payments, payments.size());
    }
}
