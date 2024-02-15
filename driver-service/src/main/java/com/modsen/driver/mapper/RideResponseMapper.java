package com.modsen.driver.mapper;

import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.dto.request.UpdateRideDriverRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideResponseMapper {
    @Mapping(target = "isDriverAvailable", constant = "true")
    UpdateRideDriverRequest toResponseWithDriver(FindDriverRequest request, Long driverId);

    @Mapping(target = "isDriverAvailable", constant = "false")
    UpdateRideDriverRequest toResponseWithoutDriver(FindDriverRequest request);
}
