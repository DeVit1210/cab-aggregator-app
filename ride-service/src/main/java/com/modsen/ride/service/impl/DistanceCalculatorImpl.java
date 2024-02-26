package com.modsen.ride.service.impl;

import com.modsen.ride.dto.request.RideCostRequest;
import com.modsen.ride.service.DistanceCalculator;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class DistanceCalculatorImpl implements DistanceCalculator {
    @Override
    public double calculateDistance(RideCostRequest request) {
        return new Random().nextDouble(0.5) * 100;
    }
}
