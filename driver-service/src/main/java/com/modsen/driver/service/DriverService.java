package com.modsen.driver.service;

import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.DriverResponseList;
import com.modsen.driver.dto.response.PagedDriverResponse;

public interface DriverService {
    DriverResponseList findAllDrivers();

    PagedDriverResponse findDrivers(int pageNumber, int pageSize, String sortField);

    DriverResponse findDriverById(Long driverId);

    DriverResponse createDriver(DriverRequest request);

    DriverResponse updateDriver(Long driverId, DriverRequest driverRequest);

    void deleteDriver(Long driverId);
}
