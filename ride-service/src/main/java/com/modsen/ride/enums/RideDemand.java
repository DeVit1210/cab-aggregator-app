package com.modsen.ride.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum RideDemand {
    HIGH(2.00, 0.33),
    MEDIUM(1.50, 0.66),
    LOW(1.00, 1.00);

    @Getter
    private final double rideCostCoefficient;
    private final double upperBound;

    public static RideDemand getDemand(double availabilityCoefficient) {
        return Arrays.stream(values())
                .filter(rideDemand -> availabilityCoefficient <= rideDemand.upperBound)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("illegal availability coefficient!"));
    }
}
