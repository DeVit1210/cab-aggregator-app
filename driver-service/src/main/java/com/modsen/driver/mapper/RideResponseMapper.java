package com.modsen.driver.mapper;

import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.dto.response.RideResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideResponseMapper {
    @Mapping(target = "isDriverAvailable", constant = "true")
    RideResponse toResponseWithDriver(RideRequest request, Long driverId);

    @Mapping(target = "isDriverAvailable", constant = "false")
    RideResponse toResponseWithoutDriver(RideRequest request);
}
