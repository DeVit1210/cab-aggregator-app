package com.modsen.ride.service.impl;

import com.modsen.ride.dto.RideCostRequest;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.dto.response.DriverAvailabilityResponse;
import com.modsen.ride.dto.response.RideCostResponse;
import com.modsen.ride.enums.RideDemand;
import com.modsen.ride.service.RideCostService;
import com.modsen.ride.service.feign.DriverServiceClient;
import com.modsen.ride.service.feign.PromocodeServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class RideCostServiceImpl implements RideCostService {
    private final DriverServiceClient driverServiceClient;
    private final PromocodeServiceClient promocodeServiceClient;
    @Value("${ride.cost.start}")
    private BigDecimal startCost;
    @Value("${ride.cost.per-kilometer}")
    private BigDecimal kilometerCost;

    @Override
    public RideCostResponse calculateCost(RideCostRequest request) {
        double distanceInKm = new Random().nextDouble(0.5) * 100;
        RideDemand rideDemand = getRideDemand();
        BigDecimal rideCost = calculateTotalCost(distanceInKm, rideDemand);
        BigDecimal discountedCost = applyPromocode(request.getPassengerId(), rideCost);

        return RideCostResponse.builder()
                .passengerId(request.getPassengerId())
                .pickUpAddress(request.getPickUpAddress())
                .destinationAddress(request.getDestinationAddress())
                .rideDemand(rideDemand)
                .distanceInKm(distanceInKm)
                .rideCost(rideCost)
                .discountedCost(discountedCost)
                .build();
    }

    private BigDecimal applyPromocode(Long passengerId, BigDecimal rideCost) {
        AppliedPromocodeResponse promocode = promocodeServiceClient.findNotConfirmedPromocode(passengerId);
        BigDecimal discountDecimal = BigDecimal.valueOf(promocode.discountPercent());
        BigDecimal discountAmount = rideCost.multiply(discountDecimal);
        return rideCost.subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private double getAvailabilityCoefficient(DriverAvailabilityResponse response) {
        return (double) response.availableDriverCount() / response.totalDriverCount();
    }

    private BigDecimal calculateTotalCost(double distanceInKm, RideDemand rideDemand) {
        BigDecimal costWithoutChargeForStart = kilometerCost.multiply(BigDecimal.valueOf(distanceInKm));
        BigDecimal basicCost = startCost.add(costWithoutChargeForStart);
        return basicCost.multiply(BigDecimal.valueOf(rideDemand.getRideCostCoefficient()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private RideDemand getRideDemand() {
        DriverAvailabilityResponse availabilityResponse = driverServiceClient.getDriverAvailability();
        double availabilityCoefficient = getAvailabilityCoefficient(availabilityResponse);
        return RideDemand.getDemand(availabilityCoefficient);
    }
}
