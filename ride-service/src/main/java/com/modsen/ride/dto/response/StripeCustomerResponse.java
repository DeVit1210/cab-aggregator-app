package com.modsen.ride.dto.response;

import lombok.Builder;

@Builder
public record StripeCustomerResponse(
        Long id,
        String stripeCustomerId
) {
}
