package com.modsen.payment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StripeCustomerResponse {
    private Long id;
    private Long passengerId;
    private String stripeCustomerId;
}
