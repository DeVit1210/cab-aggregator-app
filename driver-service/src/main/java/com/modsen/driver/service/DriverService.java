package com.modsen.driver.service;

import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.dto.response.DriverAvailabilityResponse;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;

public interface DriverService {
    DriverListResponse findAllDrivers();

    PagedDriverResponse findDrivers(int pageNumber, int pageSize, String sortField);

    DriverResponse findDriverById(Long driverId);

    DriverAvailabilityResponse getDriverAvailability();

    DriverResponse createDriver(DriverRequest request);

    DriverResponse updateDriver(Long driverId, DriverRequest driverRequest);

    void deleteDriver(Long driverId);

    void handleChangeDriverStatusRequest(ChangeDriverStatusRequest request);

    void handleFindDriverRequest(FindDriverRequest rideRequest);

    DriverResponse changeDriverStatus(ChangeDriverStatusRequest request);
}
