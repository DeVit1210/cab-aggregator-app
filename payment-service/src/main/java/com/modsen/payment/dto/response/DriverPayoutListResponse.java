package com.modsen.payment.dto.response;

import java.util.List;

public record DriverPayoutListResponse(
        List<DriverPayoutResponse> payouts,
        int size
) {
    public static DriverPayoutListResponse of(List<DriverPayoutResponse> payouts) {
        return new DriverPayoutListResponse(payouts, payouts.size());
    }
}
