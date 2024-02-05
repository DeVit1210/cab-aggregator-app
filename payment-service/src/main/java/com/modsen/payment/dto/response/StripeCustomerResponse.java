package com.modsen.payment.dto.response;

import lombok.Builder;

@Builder
public record StripeCustomerResponse(
        Long id,
        String stripeCustomerId
) {
}
