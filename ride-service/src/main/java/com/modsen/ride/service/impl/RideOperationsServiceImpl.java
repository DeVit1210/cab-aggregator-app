package com.modsen.ride.service.impl;

import com.modsen.ride.dto.request.ChangeDriverStatusRequest;
import com.modsen.ride.dto.request.FindDriverRequest;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.enums.DriverStatus;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.exception.IllegalRideStatusException;
import com.modsen.ride.kafka.producer.DriverStatusRequestProducer;
import com.modsen.ride.kafka.producer.RideRequestProducer;
import com.modsen.ride.model.Ride;
import com.modsen.ride.service.RideOperationsService;
import com.modsen.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideOperationsServiceImpl implements RideOperationsService {
    private final RideService rideService;
    private final DriverStatusRequestProducer driverStatusRequestProducer;
    private final RideRequestProducer rideRequestProducer;

    @Override
    public RideResponse acceptRide(Long rideId) {
        Ride ride = rideService.findRideById(rideId);
        validateRideStatus(ride, RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
        changeDriverStatus(ride.getDriverId(), DriverStatus.ON_WAY_TO_PASSENGER);

        return doUpdateRideStatus(ride, RideStatus.PENDING);
    }

    @Override
    public RideResponse dismissRide(Long rideId) {
        Ride ride = rideService.findRideById(rideId);
        validateRideStatus(ride, RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
        changeDriverStatus(ride.getDriverId(), DriverStatus.AVAILABLE);
        FindDriverRequest findDriverRequest = new FindDriverRequest(rideId);
        rideRequestProducer.sendRequestForDriver(findDriverRequest);

        return doUpdateRideStatus(ride, RideStatus.WITHOUT_DRIVER);
    }

    @Override
    public RideResponse notifyPassengerAboutWaiting(Long rideId) {
        Ride ride = rideService.findRideById(rideId);
        validateRideStatus(ride, RideStatus.PENDING);
        changeDriverStatus(ride.getDriverId(), DriverStatus.WAITING_FOR_PASSENGER);

        return rideService.saveRide(ride);
    }

    @Override
    public RideResponse startRide(Long rideId) {
        Ride ride = rideService.findRideById(rideId);
        validateRideStatus(ride, RideStatus.PENDING);
        changeDriverStatus(ride.getDriverId(), DriverStatus.ON_TRIP);
        ride.setStartTime(LocalDateTime.now());

        return doUpdateRideStatus(ride, RideStatus.ACTIVE);
    }

    @Override
    public RideResponse finishRide(Long rideId) {
        Ride ride = rideService.findRideById(rideId);
        validateRideStatus(ride, RideStatus.ACTIVE);
        changeDriverStatus(ride.getDriverId(), DriverStatus.AVAILABLE);
        ride.setEndTime(LocalDateTime.now());

        return doUpdateRideStatus(ride, RideStatus.FINISHED);
    }

    @Override
    public RideResponse cancelRide(Long rideId) {
        Ride ride = rideService.findRideById(rideId);
        validateRideStatus(ride, RideStatus.getNotConfirmedStatusList());
        changeDriverStatus(ride.getDriverId(), DriverStatus.AVAILABLE);

        return doUpdateRideStatus(ride, RideStatus.CANCELED);
    }

    private void changeDriverStatus(Long driverId, DriverStatus driverStatus) {
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverId(driverId)
                .driverStatus(driverStatus)
                .build();
        driverStatusRequestProducer.changeDriverStatus(request);
    }

    private RideResponse doUpdateRideStatus(Ride ride, RideStatus rideStatus) {
        ride.setRideStatus(rideStatus);
        return rideService.saveRide(ride);
    }

    private void validateRideStatus(Ride ride, RideStatus expectedRideStatus) {
        if (!ride.getRideStatus().equals(expectedRideStatus)) {
            throw new IllegalRideStatusException(expectedRideStatus);
        }
    }

    private void validateRideStatus(Ride ride, List<RideStatus> expectedRideStatuses) {
        expectedRideStatuses.stream()
                .filter(rideStatus -> ride.getRideStatus().equals(rideStatus))
                .findAny()
                .orElseThrow(() -> new IllegalRideStatusException(expectedRideStatuses.get(0)));
    }
}
