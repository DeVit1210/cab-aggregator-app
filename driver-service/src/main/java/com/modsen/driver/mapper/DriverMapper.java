package com.modsen.driver.mapper;

import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.model.Driver;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    DriverResponse toDriverResponse(Driver driver);

    Driver toDriver(DriverRequest request);

    List<DriverResponse> toDriverListResponse(List<Driver> driverList);

    default PagedDriverResponse toPagedDriverResponse(Page<Driver> driverPage) {
        return PagedDriverResponse.builder()
                .content(driverPage.getContent())
                .pageSize(driverPage.getSize())
                .pageNumber(driverPage.getNumber() + 1)
                .totalPageCount(driverPage.getTotalPages())
                .hasPrevious(driverPage.hasPrevious())
                .hasNext(driverPage.hasNext())
                .build();
    }
}
