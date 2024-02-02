package com.modsen.payment.service;

import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.dto.response.DriverPayoutListResponse;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.dto.response.Paged;

public interface DriverPayoutService {
    DriverPayoutResponse createPayout(DriverPayoutRequest request);

    DriverPayoutListResponse getAllPayoutsForDriver(Long driverId);

    Paged<DriverPayoutResponse> getAllPayouts(PageSettingsRequest request);
}
