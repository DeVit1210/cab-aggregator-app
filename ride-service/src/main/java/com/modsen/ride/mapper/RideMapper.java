package com.modsen.ride.mapper;

import com.modsen.ride.dto.request.ConfirmedRideRequest;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.response.ConfirmedRideResponse;
import com.modsen.ride.dto.response.PagedRideResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.model.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideMapper {
    Ride toRide(ConfirmedRideRequest request);

    @Mapping(target = "rideStatus", constant = "WITHOUT_DRIVER")
    Ride toRide(RideRequest request);

    RideRequest toRideRequest(Ride ride);

    RideResponse toRideResponse(Ride ride);

    ConfirmedRideResponse toConfirmedRideResponse(Ride ride);

    List<RideResponse> toRideListResponse(List<Ride> rideList);

    default PagedRideResponse toPagedRideResponse(Page<Ride> ridePage) {
        return PagedRideResponse.builder()
                .content(this.toRideListResponse(ridePage.getContent()))
                .pageNumber(ridePage.getNumber())
                .totalPages(ridePage.getTotalPages())
                .size(ridePage.getSize())
                .hasPrevious(ridePage.hasPrevious())
                .hasNext(ridePage.hasNext())
                .build();
    }
}
