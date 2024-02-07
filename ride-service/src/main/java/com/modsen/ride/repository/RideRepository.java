package com.modsen.ride.repository;

import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long>, JpaSpecificationExecutor<Ride> {
    List<Ride> findAllByDriverIdAndRideStatus(Long driverId, RideStatus rideStatus);

    List<Ride> findAllByPassengerIdAndRideStatus(Long passengerId, RideStatus rideStatus);

    Optional<Ride> findFirstByDriverIdAndRideStatus(Long driverId, RideStatus rideStatus);

    Optional<Ride> findFirstByPassengerIdAndRideStatusIn(Long passengerId, List<RideStatus> rideStatusList);

    boolean existsByPassengerIdAndRideStatusIn(Long passengerId, List<RideStatus> rideStatusList);
}
