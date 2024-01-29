package com.modsen.driver.service.impl;

import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.exception.DriverNotFoundException;
import com.modsen.driver.mapper.DriverMapper;
import com.modsen.driver.model.Driver;
import com.modsen.driver.repository.DriverRepository;
import com.modsen.driver.service.DriverService;
import com.modsen.driver.service.DriverWithSuggestedRideService;
import com.modsen.driver.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final DriverWithSuggestedRideService suggestedRideService;

    @Override
    public DriverListResponse findAllDrivers() {
        List<Driver> driverList = driverRepository.findAll();
        List<DriverResponse> driverListResponse = driverMapper.toDriverListResponse(driverList);

        return DriverListResponse.of(driverListResponse);
    }

    @Override
    public PagedDriverResponse findDrivers(int pageNumber, int pageSize, String sortField) {
        PageRequest pageRequest = PageRequestUtils.makePageRequest(pageNumber, pageSize, sortField);
        Page<Driver> driverPage = driverRepository.findAll(pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, driverPage);

        return driverMapper.toPagedDriverResponse(driverPage);
    }

    @Override
    public DriverResponse findDriverById(Long driverId) {
        return driverRepository.findById(driverId)
                .map(driverMapper::toDriverResponse)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
    }

    @Override
    public DriverResponse createDriver(DriverRequest request) {
        Driver driver = driverMapper.toDriver(request, DriverStatus.OFFLINE);
        Driver savedDriver = driverRepository.save(driver);

        return driverMapper.toDriverResponse(savedDriver);
    }

    @Override
    public DriverResponse updateDriver(Long driverId, DriverRequest driverRequest) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        driver.setFirstName(driverRequest.getFirstName());
        driver.setLastName(driverRequest.getLastName());
        driver.setEmail(driverRequest.getEmail());
        driver.setPhoneNumber(driverRequest.getPhoneNumber());
        driver.setLicenceNumber(driverRequest.getLicenceNumber());
        Driver updatedDriver = driverRepository.save(driver);

        return driverMapper.toDriverResponse(updatedDriver);
    }

    @Override
    public void deleteDriver(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        driver.ifPresentOrElse(driverRepository::delete, () -> {
            throw new DriverNotFoundException(driverId);
        });
    }

    @Override
    public Optional<Driver> findAvailableDriverForRide(RideRequest request) {
        List<Long> alreadySuggestedDriverList = suggestedRideService.getDriverIdList(request);
        if (alreadySuggestedDriverList.isEmpty()) {
            return driverRepository.findFirstByStatus(DriverStatus.AVAILABLE);
        } else {
            return driverRepository.findFirstByStatusAndIdIsNotIn(DriverStatus.AVAILABLE, alreadySuggestedDriverList);
        }
    }


    @Override
    public void updateDriverStatus(ChangeDriverStatusRequest request) {
        long driverId = request.getDriverId();
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        driver.setStatus(request.getNewStatus());
        driverRepository.save(driver);
    }
}
