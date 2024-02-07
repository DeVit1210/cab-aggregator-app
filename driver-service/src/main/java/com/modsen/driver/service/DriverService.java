package com.modsen.driver.service;

import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.model.Driver;

import java.util.Optional;

public interface DriverService {
    DriverListResponse findAllDrivers();

    PagedDriverResponse findDrivers(int pageNumber, int pageSize, String sortField);

    DriverResponse findDriverById(Long driverId);

    DriverResponse createDriver(DriverRequest request);

    DriverResponse updateDriver(Long driverId, DriverRequest driverRequest);

    void deleteDriver(Long driverId);

    Optional<Driver> findAvailableDriverForRide(RideRequest request);

    void updateDriverStatus(ChangeDriverStatusRequest request);
}
