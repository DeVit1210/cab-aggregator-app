package com.modsen.ride.service.impl;

import com.modsen.ride.dto.request.FindDriverRequest;
import com.modsen.ride.dto.request.PageSettingRequest;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.response.ConfirmedRideResponse;
import com.modsen.ride.dto.response.PagedRideResponse;
import com.modsen.ride.dto.response.RideListResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.enums.Role;
import com.modsen.ride.exception.NoAvailableRideForDriver;
import com.modsen.ride.exception.NoConfirmedRideForPassenger;
import com.modsen.ride.exception.RideNotFoundException;
import com.modsen.ride.kafka.RideRequestProducer;
import com.modsen.ride.mapper.RideMapper;
import com.modsen.ride.model.Ride;
import com.modsen.ride.repository.RideRepository;
import com.modsen.ride.service.RideService;
import com.modsen.ride.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final RideRequestProducer rideRequestProducer;

    @Override
    public PagedRideResponse findRides(PageSettingRequest request) {
        PageRequest pageRequest = PageRequestUtils.makePageRequest(request);
        Page<Ride> ridePage = rideRepository.findAll(pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, ridePage);

        return rideMapper.toPagedRideResponse(ridePage);
    }

    @Override
    public RideListResponse findAllRidesForPerson(Long personId, Role role) {
        List<Ride> rideList = role.equals(Role.DRIVER)
                ? rideRepository.findAllByDriverIdAndRideStatus(personId, RideStatus.FINISHED)
                : rideRepository.findAllByPassengerIdAndRideStatus(personId, RideStatus.FINISHED);
        List<RideResponse> rideResponseList = rideMapper.toRideListResponse(rideList);

        return RideListResponse.of(rideResponseList);
    }

    @Override
    public PagedRideResponse findRidesForPerson(Long personId, Role role, PageSettingRequest request) {
        PageRequest pageRequest = PageRequestUtils.makePageRequest(request);
        Specification<Ride> rideSpecification = buildRideSpecification(personId, role);
        Page<Ride> ridePage = rideRepository.findAll(rideSpecification, pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, ridePage);

        return rideMapper.toPagedRideResponse(ridePage);
    }

    @Override
    public RideResponse createRide(RideRequest request) {
        Ride ride = rideMapper.toRide(request);
        Ride savedRide = rideRepository.save(ride);
        FindDriverRequest findDriverRequest = new FindDriverRequest(savedRide.getId());
        rideRequestProducer.sendRequestForDriver(findDriverRequest);

        return rideMapper.toRideResponse(savedRide);
    }

    @Override
    public ConfirmedRideResponse findAvailableRideForDriver(Long driverId) {
        Ride ride = rideRepository
                .findFirstByDriverIdAndRideStatus(driverId, RideStatus.WAITING_FOR_DRIVER_CONFIRMATION)
                .orElseThrow(() -> new NoAvailableRideForDriver(driverId));

        return rideMapper.toConfirmedRideResponse(ride);
    }

    @Override
    public ConfirmedRideResponse findConfirmedRideForPassenger(Long passengerId) {
        Ride ride = rideRepository
                .findFirstByPassengerIdAndRideStatusIn(passengerId, RideStatus.getConfirmedRideStatusList())
                .orElseThrow(() -> new NoConfirmedRideForPassenger(passengerId));

        return rideMapper.toConfirmedRideResponse(ride);
    }

    @Override
    public Ride findRideById(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
    }

    @Override
    public RideResponse saveRide(Ride ride) {
        Ride savedRide = rideRepository.save(ride);
        return rideMapper.toRideResponse(savedRide);
    }

    private Specification<Ride> buildRideSpecification(Long personId, Role role) {
        return (root, query, criteriaBuilder) -> {
            String searchIdColumnName = resolveSearchColumnName(role);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get(searchIdColumnName), personId),
                    criteriaBuilder.equal(root.get("ride_status"), RideStatus.FINISHED)
            );
        };
    }

    private String resolveSearchColumnName(Role role) {
        return role.equals(Role.DRIVER) ? "driver_id" : "passenger_id";
    }
}
