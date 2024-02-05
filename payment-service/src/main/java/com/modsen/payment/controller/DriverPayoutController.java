package com.modsen.payment.controller;

import com.modsen.payment.constants.ControllerMappings;
import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.dto.response.DriverPayoutListResponse;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.service.DriverPayoutService;
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
@RequestMapping(ControllerMappings.DRIVER_PAYOUT_CONTROLLER)
@RequiredArgsConstructor
public class DriverPayoutController {
    private final DriverPayoutService driverPayoutService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverPayoutResponse createPayout(@Valid @RequestBody DriverPayoutRequest request) {
        return driverPayoutService.createPayout(request);
    }

    @GetMapping("/{driverId}")
    public DriverPayoutListResponse findAllPayoutsForDriver(@PathVariable Long driverId) {
        return driverPayoutService.getAllPayoutsForDriver(driverId);
    }

    @GetMapping("/page")
    public Paged<DriverPayoutResponse> findPayouts(PageSettingsRequest request) {
        return driverPayoutService.getAllPayouts(request);
    }
}
