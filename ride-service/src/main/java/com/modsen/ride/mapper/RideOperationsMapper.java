package com.modsen.ride.mapper;

import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.request.PaymentRequest;
import com.modsen.ride.model.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideOperationsMapper {
    @Mapping(target = "rideId", source = "ride.id")
    PaymentRequest toPaymentRequest(Ride ride, FinishRideRequest request);
}
