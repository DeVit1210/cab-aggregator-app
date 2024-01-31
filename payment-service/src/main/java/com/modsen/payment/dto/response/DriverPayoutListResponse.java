package com.modsen.payment.dto.response;

import com.modsen.payment.model.DriverPayout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverPayoutListResponse {
    private List<DriverPayout> payouts;
    private int size;

    public static DriverPayoutListResponse of(List<DriverPayout> payouts) {
        return new DriverPayoutListResponse(payouts, payouts.size());
    }
}
