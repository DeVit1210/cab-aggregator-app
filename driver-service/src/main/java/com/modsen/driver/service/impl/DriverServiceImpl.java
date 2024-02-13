package com.modsen.driver.service.impl;

import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.dto.request.UpdateRideDriverRequest;
import com.modsen.driver.dto.response.DriverAvailabilityResponse;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.exception.DriverAlreadyOnlineException;
import com.modsen.driver.exception.DriverNotAvailableException;
import com.modsen.driver.exception.DriverNotFoundException;
import com.modsen.driver.exception.DriverStatusChangeNotAllowedException;
import com.modsen.driver.kafka.producer.RideResponseProducer;
import com.modsen.driver.mapper.DriverMapper;
import com.modsen.driver.mapper.RideResponseMapper;
import com.modsen.driver.model.Driver;
import com.modsen.driver.repository.DriverRepository;
import com.modsen.driver.service.DriverService;
import com.modsen.driver.service.DriverWithSuggestedRideService;
import com.modsen.driver.service.feign.PaymentServiceClient;
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
    private final RideResponseMapper rideResponseMapper;
    private final RideResponseProducer rideResponseProducer;
    private final PaymentServiceClient paymentServiceClient;

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
        Driver driver = driverMapper.toDriver(request);
        Driver savedDriver = driverRepository.save(driver);

        return driverMapper.toDriverResponse(savedDriver);
    }

    @Override
    public DriverResponse updateDriver(Long driverId, DriverRequest driverRequest) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        driverMapper.updateDriver(driverRequest, driver);
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
    public DriverAvailabilityResponse getDriverAvailability() {
        long allDriversCount = driverRepository.count();
        long availableDriversCount = driverRepository.countAllByDriverStatus(DriverStatus.AVAILABLE);

        return new DriverAvailabilityResponse(allDriversCount, availableDriversCount);
    }

    private Optional<Driver> findAvailableDriverForRide(FindDriverRequest request) {
        List<Long> alreadySuggestedDriverList = suggestedRideService.getDriverIdList(request);

        return alreadySuggestedDriverList.isEmpty()
                ? driverRepository.findFirstByDriverStatus(DriverStatus.AVAILABLE)
                : driverRepository.findFirstByDriverStatusAndIdIsNotIn(DriverStatus.AVAILABLE, alreadySuggestedDriverList);
    }

    @Override
    public void handleChangeDriverStatusRequest(ChangeDriverStatusRequest request) {
        long driverId = request.getDriverId();
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        driver.setDriverStatus(request.getDriverStatus());
        driverRepository.save(driver);
    }

    @Override
    public void handleFindDriverRequest(FindDriverRequest rideRequest) {
        Optional<Driver> availableDriverForRide = findAvailableDriverForRide(rideRequest);
        availableDriverForRide.ifPresentOrElse(driver -> {
            driver.setDriverStatus(DriverStatus.HAS_UNCONFIRMED_RIDE);
            driverRepository.save(driver);
            suggestedRideService.save(driver, rideRequest.getRideId());
            UpdateRideDriverRequest request = rideResponseMapper.toResponseWithDriver(rideRequest, driver.getId());
            rideResponseProducer.sendUpdateRideRequest(request);
        }, () -> {
            UpdateRideDriverRequest request = rideResponseMapper.toResponseWithoutDriver(rideRequest);
            rideResponseProducer.sendUpdateRideRequest(request);
        });
    }

    @Override
    public DriverResponse changeDriverStatus(ChangeDriverStatusRequest request) {
        DriverStatus newStatus = request.getDriverStatus();
        Long driverId = request.getDriverId();
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        validateNewDriverStatus(driver, newStatus);

        return changeStatusAndSave(driver, newStatus);
    }

    private void validateNewDriverStatus(Driver driver, DriverStatus driverStatus) {
        if(!DriverStatus.allowedToBeChangedByDriver().contains(driverStatus)) {
            throw new DriverStatusChangeNotAllowedException(driverStatus);
        }

        if (driverStatus.equals(DriverStatus.AVAILABLE)) {
            validateDriverOnlineStatus(driver);
        } else {
            validateDriverOfflineStatus(driver);
        }
    }

    private void validateDriverOnlineStatus(Driver driver) {
        if (!driver.getDriverStatus().equals(DriverStatus.OFFLINE)) {
            throw new DriverAlreadyOnlineException(driver.getId());
        }
        paymentServiceClient.findAccountById(driver.getId());
    }

    private void validateDriverOfflineStatus(Driver driver) {
        if (!driver.getDriverStatus().equals(DriverStatus.AVAILABLE)) {
            throw new DriverNotAvailableException(driver.getId());
        }
    }

    private DriverResponse changeStatusAndSave(Driver driver, DriverStatus driverStatus) {
        driver.setDriverStatus(driverStatus);
        driverRepository.save(driver);

        return driverMapper.toDriverResponse(driver);
    }
}
