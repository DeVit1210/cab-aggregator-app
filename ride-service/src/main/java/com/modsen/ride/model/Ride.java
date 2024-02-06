package com.modsen.ride.model;

import com.modsen.ride.enums.RideStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long passengerId;
    private String pickUpAddress;
    private String destinationAddress;
    private BigDecimal rideCost;
    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long driverId;
}
