package com.modsen.driver.service;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.constants.TestConstants;
import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.dto.request.UpdateRideDriverRequest;
import com.modsen.driver.dto.response.AverageRatingListResponse;
import com.modsen.driver.dto.response.AverageRatingResponse;
import com.modsen.driver.dto.response.DriverAccountResponse;
import com.modsen.driver.dto.response.DriverAvailabilityResponse;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.DriverResponse;
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
import com.modsen.driver.mapper.DriverMapperImpl;
import com.modsen.driver.mapper.RideResponseMapper;
import com.modsen.driver.model.Driver;
import com.modsen.driver.repository.DriverRepository;
import com.modsen.driver.service.feign.PaymentServiceClient;
import com.modsen.driver.service.feign.RatingServiceClient;
import com.modsen.driver.service.impl.DriverServiceImpl;
import com.modsen.driver.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {
    private final Long driverId = TestConstants.DRIVER_ID;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private DriverMapperImpl driverMapper;
    @Mock
    private RatingServiceClient ratingServiceClient;
    @Mock
    private DriverWithSuggestedRideService suggestedRideService;
    @Mock
    private RideResponseMapper rideResponseMapper;
    @Mock
    private RideResponseProducer rideResponseProducer;
    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void findAllDrivers_Success() {
        List<Driver> drivers = Collections.nCopies(3, TestUtils.defaultDriver());
        List<AverageRatingResponse> averageRatingResponses =
                Collections.nCopies(3, AverageRatingResponse.empty(driverId));
        AverageRatingListResponse averageRatingListResponse =
                TestUtils.emptyAverageRatingListResponse(averageRatingResponses);

        when(driverRepository.findAll())
                .thenReturn(drivers);
        when(ratingServiceClient.findAllAverageRatings(anyString()))
                .thenReturn(averageRatingListResponse);
        when(driverMapper.toDriverListResponse(anyList(), anyList()))
                .thenCallRealMethod();

        DriverListResponse actualDriverList = driverService.findAllDrivers();

        assertNotNull(actualDriverList);
        assertEquals(drivers.size(), actualDriverList.getQuantity());
        verify(driverRepository).findAll();
        verify(ratingServiceClient).findAllAverageRatings(Role.DRIVER.name());
        verify(driverMapper).toDriverListResponse(drivers, averageRatingResponses);
    }

    @Test
    void findDriverById_DriverExists_ReturnDriver() {
        Driver driver = TestUtils.defaultDriver();
        AverageRatingResponse averageRatingResponse = AverageRatingResponse.empty(driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));
        when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(averageRatingResponse);
        when(driverMapper.toDriverResponse(any(Driver.class), any(AverageRatingResponse.class)))
                .thenCallRealMethod();

        DriverResponse actualDriver = driverService.findDriverById(driverId);

        assertNotNull(actualDriver);
        verify(driverRepository).findById(driverId);
        verify(ratingServiceClient).findAverageRating(driverId, Role.DRIVER.name());
        verify(driverMapper).toDriverResponse(driver, averageRatingResponse);
    }

    @Test
    void findDriverById_DriverDoesNotExist_ThrowDriverNotFoundException() {
        String exceptionMessage = String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID.getValue(), driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.findDriverById(driverId))
                .isInstanceOf(DriverNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void createDriver_ValidDriverRequest_ReturnCreatedDriver() {
        Driver driver = TestUtils.defaultDriver();
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        AverageRatingResponse averageRatingResponse = AverageRatingResponse.empty(driverId);

        when(driverMapper.toDriver(any(DriverRequest.class)))
                .thenReturn(driver);
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(driver);
        when(driverMapper.toDriverResponse(any(Driver.class), any(AverageRatingResponse.class)))
                .thenCallRealMethod();

        DriverResponse createdDriver = driverService.createDriver(driverRequest);

        assertNotNull(createdDriver);
        verify(driverMapper).toDriver(driverRequest);
        verify(driverRepository).save(driver);
        verify(driverMapper).toDriverResponse(driver, averageRatingResponse);
    }

    @Test
    void createDriver_DuplicateEmail_ThrowUniqueConstraintViolationException() {
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String exceptionMessage =
                String.format(MessageTemplates.EMAIL_NOT_UNIQUE.getValue(), TestConstants.DRIVER_EMAIL);

        when(driverRepository.existsByEmail(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> driverService.createDriver(driverRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);
        verify(driverRepository).existsByEmail(TestConstants.DRIVER_EMAIL);
    }

    @Test
    void createDriver_DuplicatePhoneNumber_ThrowUniqueConstraintViolationException() {
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String exceptionMessage =
                String.format(MessageTemplates.PHONE_NUMBER_NOT_UNIQUE.getValue(), TestConstants.DRIVER_PHONE_NUMBER);

        when(driverRepository.existsByPhoneNumber(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> driverService.createDriver(driverRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);
        verify(driverRepository).existsByPhoneNumber(TestConstants.DRIVER_PHONE_NUMBER);
    }

    @Test
    void createDriver_DuplicateLicenceNumber_ThrowUniqueConstraintViolationException() {
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String exceptionMessage =
                String.format(MessageTemplates.LICENCE_NOT_UNIQUE.getValue(), TestConstants.DRIVER_LICENCE_NUMBER);

        when(driverRepository.existsByLicenceNumber(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> driverService.createDriver(driverRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);
        verify(driverRepository).existsByLicenceNumber(TestConstants.DRIVER_LICENCE_NUMBER);
    }

    @Test
    void updateDriver_ValidDriverRequestAndId_ReturnUpdatedDriver() {
        Driver driver = TestUtils.defaultDriver();
        DriverRequest driverRequest = TestUtils.driverRequestWithEmail(TestConstants.DRIVER_UPDATED_EMAIL);
        AverageRatingResponse averageRatingResponse = AverageRatingResponse.empty(driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));
        doCallRealMethod().when(driverMapper)
                .updateDriver(any(DriverRequest.class), any(Driver.class));
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(driver);
        when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(averageRatingResponse);
        when(driverMapper.toDriverResponse(any(Driver.class), any(AverageRatingResponse.class)))
                .thenCallRealMethod();

        DriverResponse updatedDriver = driverService.updateDriver(driverId, driverRequest);

        assertNotNull(updatedDriver);
        assertEquals(driverRequest.getEmail(), updatedDriver.email());
        verify(driverMapper).updateDriver(driverRequest, driver);
        verify(driverRepository).findById(driverId);
        verify(driverRepository).save(driver);
        verify(ratingServiceClient).findAverageRating(driverId, Role.DRIVER.name());
        verify(driverMapper).toDriverResponse(driver, averageRatingResponse);
    }

    @Test
    void updateDriver_DuplicateEmail_ReturnUpdatedDriver() {
        Driver driver = TestUtils.defaultDriver();
        DriverRequest driverRequest = TestUtils.driverRequestWithEmail(TestConstants.DRIVER_UPDATED_EMAIL);
        String exceptionMessage =
                String.format(MessageTemplates.EMAIL_NOT_UNIQUE.getValue(), TestConstants.DRIVER_UPDATED_EMAIL);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));
        when(driverRepository.existsByEmail(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> driverService.updateDriver(driverId, driverRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void updateDriver_InvalidDriverStatus_ThrowDriverModifyingNotAllowedException() {
        Driver driver = TestUtils.driverWithStatus(DriverStatus.AVAILABLE);
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String exceptionMessage = String.format(MessageTemplates.DRIVER_MODIFYING_NOT_ALLOWED.getValue(), driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.updateDriver(driverId, driverRequest))
                .isInstanceOf(DriverModifyingNotAllowedException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void deleteDriver_ValidDriverId_Success() {
        Driver driver = TestUtils.defaultDriver();

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));
        doNothing().when(driverRepository)
                .delete(any(Driver.class));

        assertDoesNotThrow(() -> driverService.deleteDriver(driverId));
        verify(driverRepository).findById(driverId);
        verify(driverRepository).delete(driver);
    }

    @Test
    void deleteDriver_InvalidDriverStatus_ThrowDriverModifyingNotAllowedException() {
        Driver driver = TestUtils.driverWithStatus(DriverStatus.AVAILABLE);
        String exceptionMessage = String.format(MessageTemplates.DRIVER_MODIFYING_NOT_ALLOWED.getValue(), driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.deleteDriver(driverId))
                .isInstanceOf(DriverModifyingNotAllowedException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void deleteDriver_InvalidId_ThrowDriverNotFoundException() {
        String exceptionMessage = String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID.getValue(), driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.deleteDriver(driverId))
                .isInstanceOf(DriverNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void getDriversAvailability_Success() {
        when(driverRepository.count())
                .thenReturn(TestConstants.TOTAL_DRIVERS_COUNT);
        when(driverRepository.countAllByDriverStatus(any(DriverStatus.class)))
                .thenReturn(TestConstants.AVAILABLE_DRIVERS_COUNT);

        DriverAvailabilityResponse actualDriverAvailability = driverService.getDriverAvailability();

        assertEquals(TestConstants.AVAILABLE_DRIVERS_COUNT, actualDriverAvailability.availableDriverCount());
        assertEquals(TestConstants.TOTAL_DRIVERS_COUNT, actualDriverAvailability.totalDriverCount());
    }

    @Test
    void handleChangeDriverStatusRequest_ValidRequest_UpdatedDriverStatus() {
        Driver driver = TestUtils.defaultDriver();
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverStatus(DriverStatus.AVAILABLE)
                .driverId(driverId)
                .build();

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));

        driverService.handleChangeDriverStatusRequest(request);

        assertEquals(DriverStatus.AVAILABLE, driver.getDriverStatus());
        verify(driverRepository).findById(driverId);
    }

    @Test
    void handleChangeDriverStatusRequest_InvalidDriverIdInRequest_ThrowDriverNotFoundException() {
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverId(driverId)
                .build();
        String exceptionMessage = String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID.getValue(), driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.handleChangeDriverStatusRequest(request))
                .isInstanceOf(DriverNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void handleFindDriverRequest_ValidRequest_SendResponseWithDriverForRide() {
        Long rideId = TestConstants.RIDE_ID;
        Driver driver = TestUtils.defaultDriver();
        List<Long> alreadySuggestedDriverIdList = Collections.singletonList(driverId);
        UpdateRideDriverRequest updateRideDriverRequest = UpdateRideDriverRequest.builder()
                .driverId(driverId)
                .rideId(rideId)
                .isDriverAvailable(true)
                .build();
        FindDriverRequest findDriverRequest = new FindDriverRequest(rideId);

        when(suggestedRideService.getDriverIdList(any(FindDriverRequest.class)))
                .thenReturn(alreadySuggestedDriverIdList);
        when(driverRepository.findFirstByDriverStatusAndIdIsNotIn(any(DriverStatus.class), anyList()))
                .thenReturn(Optional.of(driver));
        doNothing().when(suggestedRideService)
                .save(any(Driver.class), anyLong());
        when(rideResponseMapper.toResponseWithDriver(any(FindDriverRequest.class), anyLong()))
                .thenReturn(updateRideDriverRequest);
        doNothing().when(rideResponseProducer)
                .sendUpdateRideRequest(any(UpdateRideDriverRequest.class));

        driverService.handleFindDriverRequest(findDriverRequest);

        assertEquals(DriverStatus.HAS_UNCONFIRMED_RIDE, driver.getDriverStatus());
        verify(suggestedRideService).getDriverIdList(findDriverRequest);
        verify(driverRepository).findFirstByDriverStatusAndIdIsNotIn(DriverStatus.AVAILABLE, alreadySuggestedDriverIdList);
        verify(suggestedRideService).save(driver, rideId);
        verify(rideResponseMapper).toResponseWithDriver(findDriverRequest, driverId);
        verify(rideResponseMapper, never()).toResponseWithoutDriver(any());
        verify(rideResponseProducer).sendUpdateRideRequest(updateRideDriverRequest);
    }

    @Test
    void handleFindDriverRequest_NoAvailableDrivers_SendResponseWithoutDriverForRide() {
        Long rideId = TestConstants.RIDE_ID;
        UpdateRideDriverRequest updateRideDriverRequest = UpdateRideDriverRequest.builder()
                .rideId(rideId)
                .isDriverAvailable(false)
                .build();
        FindDriverRequest findDriverRequest = new FindDriverRequest(rideId);

        when(suggestedRideService.getDriverIdList(any(FindDriverRequest.class)))
                .thenReturn(Collections.emptyList());
        when(driverRepository.findFirstByDriverStatus(any(DriverStatus.class)))
                .thenReturn(Optional.empty());
        when(rideResponseMapper.toResponseWithoutDriver(any(FindDriverRequest.class)))
                .thenReturn(updateRideDriverRequest);
        doNothing().when(rideResponseProducer)
                .sendUpdateRideRequest(any(UpdateRideDriverRequest.class));

        driverService.handleFindDriverRequest(findDriverRequest);

        verify(suggestedRideService).getDriverIdList(findDriverRequest);
        verify(driverRepository).findFirstByDriverStatus(DriverStatus.AVAILABLE);
        verify(rideResponseMapper).toResponseWithoutDriver(findDriverRequest);
        verify(rideResponseMapper, never()).toResponseWithDriver(any(), anyLong());
        verify(rideResponseProducer).sendUpdateRideRequest(updateRideDriverRequest);
    }

    @Test
    void changeDriverStatus_newStatusIsAvailableAndCurrentIsValid_UpdateDriverStatus() {
        Driver driver = TestUtils.defaultDriver();
        DriverAccountResponse driverAccountResponse = TestUtils.emptyDriverAccountResponse(driverId);
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverId(driverId)
                .driverStatus(DriverStatus.AVAILABLE)
                .build();

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));
        when(paymentServiceClient.findAccountById(anyLong()))
                .thenReturn(driverAccountResponse);
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(driver);
        when(driverMapper.toShortDriverResponse(any(Driver.class)))
                .thenCallRealMethod();

        ShortDriverResponse driverWithUpdatedStatus = driverService.changeDriverStatus(request);

        assertEquals(DriverStatus.AVAILABLE, driverWithUpdatedStatus.driverStatus());
        verify(driverRepository).findById(driverId);
        verify(paymentServiceClient).findAccountById(driverId);
        verify(driverRepository).save(driver);
        verify(driverMapper).toShortDriverResponse(driver);
    }

    @Test
    void changeDriverStatus_newStatusIsOfflineAndCurrentIsValid_UpdateDriverStatus() {
        Driver driver = TestUtils.driverWithStatus(DriverStatus.AVAILABLE);
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverId(driverId)
                .driverStatus(DriverStatus.OFFLINE)
                .build();

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(driver);
        when(driverMapper.toShortDriverResponse(any(Driver.class)))
                .thenCallRealMethod();

        ShortDriverResponse driverWithUpdatedStatus = driverService.changeDriverStatus(request);

        assertEquals(DriverStatus.OFFLINE, driverWithUpdatedStatus.driverStatus());
        verify(driverRepository).findById(driverId);
        verify(driverRepository).save(driver);
        verify(driverMapper).toShortDriverResponse(driver);
    }

    @Test
    void changeDriverStatus_InvalidNewStatus_ThrowDriverStatusChangeNotAllowedException() {
        Driver driver = TestUtils.defaultDriver();
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverStatus(DriverStatus.HAS_UNCONFIRMED_RIDE)
                .build();
        String exceptionMessage =
                String.format(MessageTemplates.STATUS_CHANGE_NOT_ALLOWED.getValue(), request.getDriverStatus());

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.changeDriverStatus(request))
                .isInstanceOf(DriverStatusChangeNotAllowedException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void changeDriverStatus_DriverAlreadyOnline_ThrowDriverAlreadyOnlineException() {
        Driver driver = TestUtils.driverWithStatus(DriverStatus.AVAILABLE);
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverStatus(DriverStatus.AVAILABLE)
                .build();
        String exceptionMessage = String.format(MessageTemplates.DRIVER_ALREADY_ONLINE.getValue(), driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.changeDriverStatus(request))
                .isInstanceOf(DriverAlreadyOnlineException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void changeDriverStatus_NewStatusIsOfflineButDriverNotAvailable_ThrowDriverNotAvailableException() {
        Driver driver = TestUtils.driverWithStatus(DriverStatus.ON_TRIP);
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverStatus(DriverStatus.OFFLINE)
                .build();
        String exceptionMessage = String.format(MessageTemplates.DRIVER_NOT_AVAILABLE.getValue(), driverId);

        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.changeDriverStatus(request))
                .isInstanceOf(DriverNotAvailableException.class)
                .hasMessage(exceptionMessage);
    }
}