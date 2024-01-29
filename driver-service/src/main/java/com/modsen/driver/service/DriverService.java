package com.modsen.driver.service;

import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.model.Driver;

import java.util.List;

public interface DriverService {
    DriverListResponse findAllDrivers();

    PagedDriverResponse findDrivers(int pageNumber, int pageSize, String sortField);

    DriverResponse findDriverById(Long driverId);

    DriverResponse createDriver(DriverRequest request);

    DriverResponse updateDriver(Long driverId, DriverRequest driverRequest);

    void deleteDriver(Long driverId);

    Driver findDriverByStatus(DriverStatus status);

    Driver findDriverByStatus(DriverStatus status, List<Long> driverToExcludeIdList);

    void updateDriverStatus(ChangeDriverStatusRequest request);
}
