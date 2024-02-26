package com.modsen.ride.service.impl;

import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.FindDriverRequest;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.request.UpdateRideDriverRequest;
import com.modsen.ride.dto.response.RideListResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.dto.response.ShortRideResponse;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.enums.Role;
import com.modsen.ride.exception.NoAvailableRideForDriver;
import com.modsen.ride.exception.NoConfirmedRideForPassenger;
import com.modsen.ride.exception.NotFinishedRideAlreadyExistsException;
import com.modsen.ride.exception.RideNotFoundException;
import com.modsen.ride.exception.base.NotFoundException;
import com.modsen.ride.kafka.producer.RideRequestProducer;
import com.modsen.ride.mapper.RideMapper;
import com.modsen.ride.model.Ride;
import com.modsen.ride.repository.RideRepository;
import com.modsen.ride.service.feign.PassengerServiceClient;
import com.modsen.ride.service.feign.PaymentServiceClient;
import com.modsen.ride.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private RideMapper rideMapper;
    @Mock
    private RideRequestProducer rideRequestProducer;
    @Mock
    private PassengerServiceClient passengerServiceClient;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    void findAllRidesForPerson_ExistsAtLeastOne_ReturnRides() {
        Long driverId = TestConstants.DRIVER_ID;
        List<Ride> rideList = Collections.nCopies(3, TestUtils.defaultRide());
        List<RideResponse> rideListResponse = Collections.nCopies(3, TestUtils.defaultRideResponse());

        when(rideRepository.findAllByDriverIdAndRideStatus(anyLong(), any(RideStatus.class)))
                .thenReturn(rideList);
        when(rideMapper.toRideListResponse(anyList()))
                .thenReturn(rideListResponse);

        RideListResponse actualRideList = rideService.findAllRidesForPerson(driverId, Role.DRIVER);

        assertNotNull(actualRideList);
        assertEquals(rideList.size(), actualRideList.getQuantity());
        verify(rideRepository).findAllByDriverIdAndRideStatus(driverId, RideStatus.FINISHED);
        verify(rideRepository, never()).findAllByPassengerIdAndRideStatus(anyLong(), any(RideStatus.class));
        verify(rideMapper).toRideListResponse(rideList);
    }

    @Test
    void findRide_RideExists_ReturnRide() {
        Long rideId = TestConstants.RIDE_ID;
        Ride ride = TestUtils.defaultRide();
        RideResponse rideResponse = TestUtils.defaultRideResponse();

        when(rideRepository.findById(anyLong()))
                .thenReturn(Optional.of(ride));
        when(rideMapper.toRideResponse(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse actualRide = rideService.findRide(rideId);

        assertNotNull(actualRide);
        verify(rideRepository).findById(rideId);
        verify(rideMapper).toRideResponse(ride);
    }

    @Test
    void findRide_RideDoesNotExist_ThrowRideNotFoundException() {
        Long rideId = TestConstants.RIDE_ID;
        String exceptionMessage = String.format(MessageTemplates.RIDE_NOT_FOUND_BY_ID.getValue(), rideId);

        when(rideRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rideService.findRide(rideId))
                .isInstanceOf(RideNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void findAvailableRideForDriver_AvailableRideExists_ReturnConfirmedRide() {
        Long driverId = TestConstants.DRIVER_ID;
        Ride ride = TestUtils.rideWithStatus(RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
        ShortRideResponse shortRideResponse = TestUtils.defaultShortRideResponse();

        when(rideRepository.findFirstByDriverIdAndRideStatus(anyLong(), any(RideStatus.class)))
                .thenReturn(Optional.of(ride));
        when(rideMapper.toShortRideResponse(ride))
                .thenReturn(shortRideResponse);

        ShortRideResponse actualShortRideResponse = rideService.findAvailableRideForDriver(driverId);

        assertNotNull(actualShortRideResponse);
        verify(rideRepository).findFirstByDriverIdAndRideStatus(driverId, RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
        verify(rideMapper).toShortRideResponse(ride);
    }

    @Test
    void findAvailableRideForDriver_NoAvailableRides_ThrowNoAvailableRideForDriverException() {
        Long driverId = TestConstants.DRIVER_ID;
        String exceptionMessage = String.format(MessageTemplates.NO_AVAILABLE_RIDE_FOR_DRIVER.getValue(), driverId);

        when(rideRepository.findFirstByDriverIdAndRideStatus(anyLong(), any(RideStatus.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rideService.findAvailableRideForDriver(driverId))
                .isInstanceOf(NoAvailableRideForDriver.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void findConfirmedRideForPassenger_ConfirmedRideExists_ReturnConfirmedRide() {
        Long passengerId = TestConstants.PASSENGER_ID;
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        ShortRideResponse shortRideResponse = TestUtils.defaultShortRideResponse();

        when(rideRepository.findFirstByPassengerIdAndRideStatusIn(anyLong(), anyList()))
                .thenReturn(Optional.of(ride));
        when(rideMapper.toShortRideResponse(ride))
                .thenReturn(shortRideResponse);

        ShortRideResponse actualShortRideResponse = rideService.findConfirmedRideForPassenger(passengerId);

        assertNotNull(actualShortRideResponse);
        verify(rideRepository).findFirstByPassengerIdAndRideStatusIn(passengerId, RideStatus.getConfirmedRideStatusList());
        verify(rideMapper).toShortRideResponse(ride);
    }

    @Test
    void findConfirmedRideForPassenger_NoConfirmedRides_ThrowNoConfirmedRideForPassenger() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String exceptionMessage = String.format(MessageTemplates.NO_ACTIVE_RIDE_FOR_USER.getValue(), passengerId);

        when(rideRepository.findFirstByPassengerIdAndRideStatusIn(anyLong(), anyList()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rideService.findConfirmedRideForPassenger(passengerId))
                .isInstanceOf(NoConfirmedRideForPassenger.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void createRide_ValidRideRequest_ReturnCreatedRide() {
        Ride ride = TestUtils.defaultRide();
        RideRequest rideRequest = TestUtils.defaultRideRequest();
        RideResponse rideResponse = TestUtils.defaultRideResponse();
        FindDriverRequest findDriverRequest = TestUtils.defaultFindDriverRequest();

        when(rideMapper.toRide(any(RideRequest.class)))
                .thenReturn(ride);
        when(rideRepository.save(any(Ride.class)))
                .thenReturn(ride);
        doNothing().when(rideRequestProducer)
                .sendRequestForDriver(any(FindDriverRequest.class));
        when(rideMapper.toRideResponse(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse createdRide = rideService.createRide(rideRequest);

        assertNotNull(createdRide);
        verify(rideMapper).toRide(rideRequest);
        verify(rideRepository).save(ride);
        verify(rideRequestProducer).sendRequestForDriver(findDriverRequest);
        verify(rideMapper).toRideResponse(ride);
    }

    @Test
    void createRide_PassengerDoesNotExist_ThrowNotFoundException() {
        RideRequest rideRequest = TestUtils.defaultRideRequest();

        when(passengerServiceClient.findPassengerById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrowsExactly(NotFoundException.class, () -> rideService.createRide(rideRequest));
        verify(passengerServiceClient).findPassengerById(TestConstants.PASSENGER_ID);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void createRide_PassengerAlreadyTookOneRide_ThrowNotFinishedRideAlreadyExistsException() {
        Long passengerId = TestConstants.PASSENGER_ID;
        RideRequest rideRequest = TestUtils.defaultRideRequest();
        String exceptionMessage = String.format(
                MessageTemplates.NOT_FINISHED_RIDE_EXISTS_FOR_PASSENGER.getValue(),
                passengerId
        );

        when(rideRepository.existsByPassengerIdAndRideStatusIn(anyLong(), anyList()))
                .thenReturn(true);

        assertThatThrownBy(() -> rideService.createRide(rideRequest))
                .isInstanceOf(NotFinishedRideAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
        verify(rideRepository).existsByPassengerIdAndRideStatusIn(passengerId, RideStatus.getNotFinishedStatusList());
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void createRide_StripeAccountNotCreatedForPassenger_ThrowNotFoundException() {
        Long passengerId = TestConstants.PASSENGER_ID;
        RideRequest rideRequest = TestUtils.defaultRideRequest();

        when(paymentServiceClient.findStripeCustomerById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrowsExactly(NotFoundException.class, () -> rideService.createRide(rideRequest));
        verify(paymentServiceClient).findStripeCustomerById(passengerId);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void createRide_PassengerHasNoDefaultCreditCard_ThrowNotFoundException() {
        Long passengerId = TestConstants.PASSENGER_ID;
        RideRequest rideRequest = TestUtils.defaultRideRequest();

        when(paymentServiceClient.getDefaultCreditCard(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrowsExactly(NotFoundException.class, () -> rideService.createRide(rideRequest));
        verify(paymentServiceClient).getDefaultCreditCard(passengerId);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void handleUpdateRideDriverRequest_ValidRequest_AssignDriverToRide() {
        Long rideId = TestConstants.RIDE_ID;
        Ride ride = Ride.builder().build();
        RideResponse rideResponse = TestUtils.defaultRideResponse();
        UpdateRideDriverRequest request = TestUtils.updateRideDriverRequestWithDriver();
        RideStatus expectedRideStatus = RideStatus.WAITING_FOR_DRIVER_CONFIRMATION;

        when(rideRepository.findById(anyLong()))
                .thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class)))
                .thenReturn(ride);
        when(rideMapper.toRideResponse(any(Ride.class)))
                .thenReturn(rideResponse);

        rideService.handleUpdateDriver(request);

        assertEquals(expectedRideStatus, ride.getRideStatus());
        assertEquals(TestConstants.DRIVER_ID, ride.getDriverId());
        verify(rideRepository).findById(rideId);
        verify(rideRepository).save(ride);
        verify(rideMapper).toRideResponse(ride);
    }

    @Test
    void handleUpdateRideDriverRequest_NoAvailableDriver_SendAnotherRequestForDriver() {
        UpdateRideDriverRequest updateRideDriverRequest = TestUtils.updateRideDriverRequestWithoutDriver();
        FindDriverRequest findDriverRequest = TestUtils.defaultFindDriverRequest();

        doNothing().when(rideRequestProducer)
                .sendRequestForDriver(any(FindDriverRequest.class));

        rideService.handleUpdateDriver(updateRideDriverRequest);

        verify(rideRequestProducer).sendRequestForDriver(findDriverRequest);
        verify(rideRepository, never()).findById(anyLong());
        verify(rideRepository, never()).save(any(Ride.class));
    }
}