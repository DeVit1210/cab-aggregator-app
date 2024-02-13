package com.modsen.driver.mapper;

import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.response.AverageRatingResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.dto.response.ShortDriverResponse;
import com.modsen.driver.model.Driver;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {
    @Mapping(target = "id", source = "driver.id")
    DriverResponse toDriverResponse(Driver driver, AverageRatingResponse response);

    ShortDriverResponse toShortDriverResponse(Driver driver);

    @Mapping(target = "driverStatus", constant = "OFFLINE")
    Driver toDriver(DriverRequest request);

    List<DriverResponse> toDriverListResponse(List<Driver> driverList, List<AverageRatingResponse> averageRatingList);

    List<ShortDriverResponse> toShortDriverListResponse(List<Driver> driverList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDriver(DriverRequest request, @MappingTarget Driver driver);

    default PagedDriverResponse toPagedDriverResponse(Page<Driver> driverPage) {
        return PagedDriverResponse.builder()
                .content(toShortDriverListResponse(driverPage.getContent()))
                .pageSize(driverPage.getSize())
                .pageNumber(driverPage.getNumber() + 1)
                .totalPageCount(driverPage.getTotalPages())
                .hasPrevious(driverPage.hasPrevious())
                .hasNext(driverPage.hasNext())
                .build();
    }
}
