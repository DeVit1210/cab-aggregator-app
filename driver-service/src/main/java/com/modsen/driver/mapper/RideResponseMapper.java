package com.modsen.driver.mapper;

import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.dto.response.RideResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RideResponseMapper {
    @Mapping(target = "isDriverAvailable", constant = "true")
    RideResponse toResponseWithDriver(RideRequest request, Long driverId);

    @Mapping(target = "isDriverAvailable", constant = "false")
    RideResponse toResponseWithoutDriver(RideRequest request);
}
