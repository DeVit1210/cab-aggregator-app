package com.modsen.ride.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum RideDemand {
    LOW(1.00, 0.00, 0.33),
    MEDIUM(1.50, 0.33, 0.66),
    HIGH(2.00, 0.66, 1.00);

    @Getter
    private final double rideCostCoefficient;
    private final double lowerBound;
    private final double upperBound;

    public static RideDemand getDemand(double availabilityCoefficient) {
        return Arrays.stream(values())
                .filter(rideDemand -> availabilityCoefficient > rideDemand.lowerBound
                        && availabilityCoefficient <= rideDemand.upperBound
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("illegal availability coefficient!"));
    }
}
