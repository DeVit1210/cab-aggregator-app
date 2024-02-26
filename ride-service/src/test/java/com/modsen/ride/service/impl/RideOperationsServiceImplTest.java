package com.modsen.ride.service.impl;

import com.modsen.ride.constants.ExceptionConstants;
import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.ChangeDriverStatusRequest;
import com.modsen.ride.dto.request.FindDriverRequest;
import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.request.PaymentRequest;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.enums.DriverStatus;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.exception.IllegalRideStatusException;
import com.modsen.ride.exception.RideNotFoundException;
import com.modsen.ride.exception.base.ConflictException;
import com.modsen.ride.exception.base.NotFoundException;
import com.modsen.ride.kafka.producer.DriverStatusRequestProducer;
import com.modsen.ride.kafka.producer.RideRequestProducer;
import com.modsen.ride.mapper.RideOperationsMapper;
import com.modsen.ride.model.Ride;
import com.modsen.ride.service.RideService;
import com.modsen.ride.service.feign.PaymentServiceClient;
import com.modsen.ride.service.feign.PromocodeServiceClient;
import com.modsen.ride.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideOperationsServiceImplTest {
    private static final LocalDateTime currentTime = LocalDateTime.now();
    private final Long rideId = TestConstants.RIDE_ID;
    @Mock
    private RideService rideService;
    @Mock
    private RideOperationsMapper rideOperationsMapper;
    @Mock
    private DriverStatusRequestProducer driverStatusRequestProducer;
    @Mock
    private RideRequestProducer rideRequestProducer;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private PromocodeServiceClient promocodeServiceClient;
    @InjectMocks
    private RideOperationsServiceImpl rideOperationsService;

    @BeforeAll
    static void beforeAll() {
        mockStatic(LocalDateTime.class).when(LocalDateTime::now)
                .thenReturn(currentTime);
    }

    @Test
    void acceptRide_ValidCurrentRideStatus_ShouldAcceptRide() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
        ChangeDriverStatusRequest changeDriverStatusRequest =
                TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.ON_WAY_TO_PASSENGER);
        RideResponse rideResponse = TestUtils.defaultRideResponse();

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        doNothing().when(driverStatusRequestProducer)
                .changeDriverStatus(any(ChangeDriverStatusRequest.class));
        when(rideService.saveRide(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse actualRide = rideOperationsService.acceptRide(rideId);

        assertNotNull(actualRide);
        assertEquals(RideStatus.PENDING, ride.getRideStatus());
        verify(rideService).findRideById(rideId);
        verify(driverStatusRequestProducer).changeDriverStatus(changeDriverStatusRequest);
        verify(rideService).saveRide(ride);
    }

    @Test
    void acceptRide_InvalidCurrentRideStatus_ThrowIllegalRideStatusException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        String exceptionMessage = ExceptionConstants.WAITING_FOR_CONFIRMATION_STATE_REQUIRED;

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);

        assertThatThrownBy(() -> rideOperationsService.acceptRide(rideId))
                .isInstanceOf(IllegalRideStatusException.class)
                .hasMessage(exceptionMessage);
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }

    @Test
    void acceptRide_RideNotFound_ThrowRideNotFoundException() {
        when(rideService.findRideById(anyLong()))
                .thenThrow(RideNotFoundException.class);
        assertThrowsExactly(RideNotFoundException.class, () -> rideOperationsService.acceptRide(rideId));
    }

    @Test
    void dismissRide_ValidCurrentRideStatus_ShouldDismissRide() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
        ChangeDriverStatusRequest changeDriverStatusRequest =
                TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.AVAILABLE);
        FindDriverRequest findDriverRequest = TestUtils.defaultFindDriverRequest();
        RideResponse rideResponse = TestUtils.defaultRideResponse();
        RideStatus expectedNewRideStatus = RideStatus.WITHOUT_DRIVER;

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        doNothing().when(driverStatusRequestProducer)
                .changeDriverStatus(any(ChangeDriverStatusRequest.class));
        doNothing().when(rideRequestProducer)
                .sendRequestForDriver(any(FindDriverRequest.class));
        when(rideService.saveRide(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse actualRide = rideOperationsService.dismissRide(rideId);

        assertNotNull(actualRide);
        assertEquals(expectedNewRideStatus, ride.getRideStatus());
        verify(rideService).findRideById(rideId);
        verify(driverStatusRequestProducer).changeDriverStatus(changeDriverStatusRequest);
        verify(rideRequestProducer).sendRequestForDriver(findDriverRequest);
        verify(rideService).saveRide(ride);
    }

    @Test
    void dismissRide_InvalidCurrentRideStatus_ThrowIllegalRideStatusException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        String exceptionMessage = ExceptionConstants.WAITING_FOR_CONFIRMATION_STATE_REQUIRED;

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);

        assertThatThrownBy(() -> rideOperationsService.dismissRide(rideId))
                .isInstanceOf(IllegalRideStatusException.class)
                .hasMessage(exceptionMessage);
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }

    @Test
    void notifyPassengerAboutWaiting_ValidCurrentRideStatus_ShouldNotifyPassenger() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.PENDING);
        ChangeDriverStatusRequest changeDriverStatusRequest =
                TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.WAITING_FOR_PASSENGER);
        RideResponse rideResponse = TestUtils.defaultRideResponse();

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        doNothing().when(driverStatusRequestProducer)
                .changeDriverStatus(any(ChangeDriverStatusRequest.class));
        when(rideService.saveRide(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse actualRide = rideOperationsService.notifyPassengerAboutWaiting(rideId);

        assertNotNull(actualRide);
        assertEquals(RideStatus.PENDING, ride.getRideStatus());
        verify(rideService).findRideById(rideId);
        verify(driverStatusRequestProducer).changeDriverStatus(changeDriverStatusRequest);
        verify(rideService).saveRide(ride);
    }

    @Test
    void notifyPassengerAboutWaiting_InvalidCurrentRideStatus_ThrowIllegalRideStatusException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        String exceptionMessage = ExceptionConstants.PENDING_STATUS_REQUIRED;

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);

        assertThatThrownBy(() -> rideOperationsService.notifyPassengerAboutWaiting(rideId))
                .isInstanceOf(IllegalRideStatusException.class)
                .hasMessage(exceptionMessage);
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }

    @Test
    void startRide_ValidCurrentRideStatus_ShouldStartRide() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.PENDING);
        RideStatus expectedNewRideStatus = RideStatus.ACTIVE;
        ChangeDriverStatusRequest changeDriverStatusRequest =
                TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.ON_TRIP);
        RideResponse rideResponse = TestUtils.defaultRideResponse();

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        doNothing().when(driverStatusRequestProducer)
                .changeDriverStatus(any(ChangeDriverStatusRequest.class));
        when(rideService.saveRide(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse actualRide = rideOperationsService.startRide(rideId);

        assertNotNull(actualRide);
        assertEquals(expectedNewRideStatus, ride.getRideStatus());
        assertEquals(currentTime, ride.getStartTime());
        verify(rideService).findRideById(rideId);
        verify(driverStatusRequestProducer).changeDriverStatus(changeDriverStatusRequest);
        verify(rideService).saveRide(ride);
    }

    @Test
    void startRide_InvalidCurrentRideStatus_ThrowIllegalRideStatusException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        String exceptionMessage = ExceptionConstants.PENDING_STATUS_REQUIRED;

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);

        assertThatThrownBy(() -> rideOperationsService.startRide(rideId))
                .isInstanceOf(IllegalRideStatusException.class)
                .hasMessage(exceptionMessage);
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }

    @Test
    void finishRide_ValidCurrentRideStatus_ShouldFinishRide() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        ChangeDriverStatusRequest changeDriverStatusRequest =
                TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.AVAILABLE);
        FinishRideRequest finishRideRequest = TestUtils.defaultFinishRideRequest();
        RideStatus expectedNewRideStatus = RideStatus.FINISHED;
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();
        AppliedPromocodeResponse appliedPromocodeResponse = TestUtils.defaultAppliedPromocodeResponse();
        RideResponse rideResponse = TestUtils.defaultRideResponse();

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        when(rideOperationsMapper.toPaymentRequest(any(Ride.class), any(FinishRideRequest.class)))
                .thenReturn(paymentRequest);
        when(promocodeServiceClient.findNotConfirmedPromocode(anyLong()))
                .thenReturn(appliedPromocodeResponse);
        doNothing().when(driverStatusRequestProducer)
                .changeDriverStatus(any(ChangeDriverStatusRequest.class));
        when(rideService.saveRide(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse actualRide = rideOperationsService.finishRide(finishRideRequest);

        assertNotNull(actualRide);
        assertEquals(expectedNewRideStatus, ride.getRideStatus());
        assertEquals(currentTime, ride.getEndTime());
        verify(rideService).findRideById(finishRideRequest.getId());
        verify(paymentServiceClient).createPayment(paymentRequest);
        verify(promocodeServiceClient).findNotConfirmedPromocode(TestConstants.PASSENGER_ID);
        verify(promocodeServiceClient).confirmPromocodeAppliance(anyLong());
        verify(driverStatusRequestProducer).changeDriverStatus(changeDriverStatusRequest);
        verify(rideService).saveRide(ride);
    }

    @Test
    void finishRide_InvalidCurrentRideStatus_ThrowIllegalRideStatusException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.FINISHED);
        String exceptionMessage = ExceptionConstants.ACTIVE_STATUS_REQUIRED;
        FinishRideRequest finishRideRequest = TestUtils.defaultFinishRideRequest();

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);

        assertThatThrownBy(() -> rideOperationsService.finishRide(finishRideRequest))
                .isInstanceOf(IllegalRideStatusException.class)
                .hasMessage(exceptionMessage);
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }

    @Test
    void finishRide_ErrorWhileProcessingPayment_ThrowConflictException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        FinishRideRequest finishRideRequest = TestUtils.defaultFinishRideRequest();
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        when(rideOperationsMapper.toPaymentRequest(any(Ride.class), any(FinishRideRequest.class)))
                .thenReturn(paymentRequest);
        when(paymentServiceClient.createPayment(any(PaymentRequest.class)))
                .thenThrow(ConflictException.class);

        assertThrowsExactly(ConflictException.class, () -> rideOperationsService.finishRide(finishRideRequest));
        verify(rideService).findRideById(finishRideRequest.getId());
        verify(rideOperationsMapper).toPaymentRequest(ride, finishRideRequest);
        verify(paymentServiceClient).createPayment(paymentRequest);
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }

    @Test
    void finishRide_AppliedPromocodeNotFound_ThrowNotFoundException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
        FinishRideRequest finishRideRequest = TestUtils.defaultFinishRideRequest();
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        when(rideOperationsMapper.toPaymentRequest(any(Ride.class), any(FinishRideRequest.class)))
                .thenReturn(paymentRequest);
        when(promocodeServiceClient.findNotConfirmedPromocode(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrowsExactly(NotFoundException.class, () -> rideOperationsService.finishRide(finishRideRequest));
        verify(rideService).findRideById(finishRideRequest.getId());
        verify(rideOperationsMapper).toPaymentRequest(ride, finishRideRequest);
        verify(promocodeServiceClient).findNotConfirmedPromocode(TestConstants.PASSENGER_ID);
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }

    @ParameterizedTest
    @EnumSource(value = RideStatus.class, names = {"WAITING_FOR_DRIVER_CONFIRMATION", "WITHOUT_DRIVER", "PENDING"})
    void cancelRide_ValidCurrentRideStatus_ShouldCancelRide(RideStatus currentRideStatus) {
        Ride ride = TestUtils.rideWithStatus(currentRideStatus);
        RideResponse rideResponse = TestUtils.defaultRideResponse();
        ChangeDriverStatusRequest changeDriverStatusRequest =
                TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.AVAILABLE);
        RideStatus expectedNewRideStatus = RideStatus.CANCELED;

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        doNothing().when(driverStatusRequestProducer)
                .changeDriverStatus(any(ChangeDriverStatusRequest.class));
        when(rideService.saveRide(any(Ride.class)))
                .thenReturn(rideResponse);

        RideResponse actualRide = rideOperationsService.cancelRide(rideId);

        assertNotNull(actualRide);
        assertEquals(expectedNewRideStatus, ride.getRideStatus());
        verify(rideService).findRideById(rideId);
        verify(driverStatusRequestProducer).changeDriverStatus(changeDriverStatusRequest);
        verify(rideService).saveRide(ride);
    }

    @Test
    void cancelRide_InvalidCurrentRideStatus_ThrowIllegalRideStatusException() {
        Ride ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);

        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);

        assertThrowsExactly(IllegalRideStatusException.class, () -> rideOperationsService.cancelRide(rideId));
        verify(driverStatusRequestProducer, never()).changeDriverStatus(any(ChangeDriverStatusRequest.class));
        verify(rideService, never()).saveRide(any(Ride.class));
    }
}