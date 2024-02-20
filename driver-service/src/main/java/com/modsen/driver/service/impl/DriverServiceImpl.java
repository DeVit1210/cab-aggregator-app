package com.modsen.driver.service.impl;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.dto.request.UpdateRideDriverRequest;
import com.modsen.driver.dto.response.AverageRatingResponse;
import com.modsen.driver.dto.response.DriverAvailabilityResponse;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.dto.response.ShortDriverResponse;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.enums.Role;
import com.modsen.driver.exception.DriverAlreadyOnlineException;
import com.modsen.driver.exception.DriverModifyingNotAllowedException;
import com.modsen.driver.exception.DriverNotAvailableException;
import com.modsen.driver.exception.DriverNotFoundException;
import com.modsen.driver.exception.DriverStatusChangeNotAllowedException;
import com.modsen.driver.exception.UniqueConstraintViolationException;
import com.modsen.driver.kafka.producer.RideResponseProducer;
import com.modsen.driver.mapper.DriverMapper;
import com.modsen.driver.mapper.RideResponseMapper;
import com.modsen.driver.model.Driver;
import com.modsen.driver.repository.DriverRepository;
import com.modsen.driver.service.DriverService;
import com.modsen.driver.service.DriverWithSuggestedRideService;
import com.modsen.driver.service.feign.PaymentServiceClient;
import com.modsen.driver.service.feign.RatingServiceClient;
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
    private final RatingServiceClient ratingServiceClient;

    @Override
    public DriverListResponse findAllDrivers() {
        List<Driver> driverList = driverRepository.findAll();
        List<AverageRatingResponse> averageRatingList = ratingServiceClient
                .findAllAverageRatings(Role.DRIVER.name())
                .averageRatingResponses();
        List<DriverResponse> driverListResponse = driverMapper.toDriverListResponse(driverList, averageRatingList);

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
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        AverageRatingResponse averageRating = ratingServiceClient.findAverageRating(driverId, Role.DRIVER.name());

        return driverMapper.toDriverResponse(driver, averageRating);
    }

    @Override
    public DriverResponse createDriver(DriverRequest request) {
        validateDriverRequest(request);
        Driver driver = driverMapper.toDriver(request);
        Driver savedDriver = driverRepository.save(driver);
        AverageRatingResponse defaultAverageRating = AverageRatingResponse.empty(savedDriver.getId());

        return driverMapper.toDriverResponse(savedDriver, defaultAverageRating);
    }

    @Override
    public DriverResponse updateDriver(Long driverId, DriverRequest driverRequest) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        validateDriverModifyingAllowance(driver);
        validateDriverRequest(driverRequest, driver);
        driverMapper.updateDriver(driverRequest, driver);
        Driver updatedDriver = driverRepository.save(driver);
        AverageRatingResponse averageRating = ratingServiceClient.findAverageRating(driverId, Role.DRIVER.name());

        return driverMapper.toDriverResponse(updatedDriver, averageRating);
    }

    @Override
    public void deleteDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        validateDriverModifyingAllowance(driver);
        driverRepository.delete(driver);
    }

    @Override
    public DriverAvailabilityResponse getDriverAvailability() {
        long allDriversCount = driverRepository.count();
        long availableDriversCount = driverRepository.countAllByDriverStatus(DriverStatus.AVAILABLE);

        return new DriverAvailabilityResponse(allDriversCount, availableDriversCount);
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
    public ShortDriverResponse changeDriverStatus(ChangeDriverStatusRequest request) {
        DriverStatus newStatus = request.getDriverStatus();
        Long driverId = request.getDriverId();
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        validateNewDriverStatus(driver, newStatus);

        return changeStatusAndSave(driver, newStatus);
    }

    private Optional<Driver> findAvailableDriverForRide(FindDriverRequest request) {
        List<Long> alreadySuggestedDriverList = suggestedRideService.getDriverIdList(request);

        return alreadySuggestedDriverList.isEmpty()
                ? driverRepository.findFirstByDriverStatus(DriverStatus.AVAILABLE)
                : driverRepository.findFirstByDriverStatusAndIdIsNotIn(DriverStatus.AVAILABLE, alreadySuggestedDriverList);
    }

    private void validateNewDriverStatus(Driver driver, DriverStatus driverStatus) {
        if (!DriverStatus.allowedToBeChangedByDriver().contains(driverStatus)) {
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

    private ShortDriverResponse changeStatusAndSave(Driver driver, DriverStatus driverStatus) {
        driver.setDriverStatus(driverStatus);
        driverRepository.save(driver);

        return driverMapper.toShortDriverResponse(driver);
    }

    private void validateDriverModifyingAllowance(Driver driver) {
        if (!driver.getDriverStatus().equals(DriverStatus.OFFLINE)) {
            throw new DriverModifyingNotAllowedException(driver.getId());
        }
    }

    private void validateDriverRequest(DriverRequest request) {
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        String licenceNumber = request.getLicenceNumber();

        if (driverRepository.existsByEmail(email)) {
            throw new UniqueConstraintViolationException(MessageTemplates.EMAIL_NOT_UNIQUE, email);
        }
        if (driverRepository.existsByPhoneNumber(phoneNumber)) {
            throw new UniqueConstraintViolationException(MessageTemplates.PHONE_NUMBER_NOT_UNIQUE, phoneNumber);
        }
        if (driverRepository.existsByLicenceNumber(licenceNumber)) {
            throw new UniqueConstraintViolationException(MessageTemplates.LICENCE_NOT_UNIQUE, licenceNumber);
        }
    }

    private void validateDriverRequest(DriverRequest request, Driver driver) {
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        String licenceNumber = request.getLicenceNumber();

        if (driverRepository.existsByEmail(email) && !driver.getEmail().equals(email)) {
            throw new UniqueConstraintViolationException(MessageTemplates.EMAIL_NOT_UNIQUE, email);
        }
        if (driverRepository.existsByPhoneNumber(phoneNumber) && !driver.getPhoneNumber().equals(phoneNumber)) {
            throw new UniqueConstraintViolationException(MessageTemplates.PHONE_NUMBER_NOT_UNIQUE, phoneNumber);
        }
        if (driverRepository.existsByLicenceNumber(licenceNumber) && !driver.getLicenceNumber().equals(licenceNumber)) {
            throw new UniqueConstraintViolationException(MessageTemplates.LICENCE_NOT_UNIQUE, licenceNumber);
        }
    }
}
