package com.modsen.ride.controller;

import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.request.RideCostRequest;
import com.modsen.ride.dto.response.RideCostResponse;
import com.modsen.ride.service.RideCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ServiceMappings.RIDE_COST_CONTROLLER)
@RequiredArgsConstructor
public class RideCostController {
    private final RideCostService rideCostService;

    @PostMapping
    public RideCostResponse calculateRideCost(@RequestBody RideCostRequest request) {
        return rideCostService.calculateCost(request);
    }
}
