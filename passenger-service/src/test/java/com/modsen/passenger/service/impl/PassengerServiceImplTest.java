package com.modsen.passenger.service.impl;

import com.modsen.passenger.constants.MessageTemplates;
import com.modsen.passenger.constants.TestConstants;
import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.AverageRatingListResponse;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.enums.Role;
import com.modsen.passenger.exception.PassengerNotFoundException;
import com.modsen.passenger.exception.UniqueConstraintViolationException;
import com.modsen.passenger.mapper.PassengerMapperImpl;
import com.modsen.passenger.model.Passenger;
import com.modsen.passenger.repository.PassengerRepository;
import com.modsen.passenger.service.feign.RatingServiceClient;
import com.modsen.passenger.utils.TestUtils;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private PassengerMapperImpl passengerMapper;
    @Mock
    private RatingServiceClient ratingServiceClient;
    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void findPassengerById_ValidId_PassengerFound() {
        Long passengerId = TestConstants.PASSENGER_ID;
        Passenger passenger = TestUtils.defaultPassenger();
        AverageRatingResponse averageRatingResponse = AverageRatingResponse.empty(passengerId);

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.of(passenger));
        when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(averageRatingResponse);
        when(passengerMapper.toPassengerResponse(any(Passenger.class), any(AverageRatingResponse.class)))
                .thenCallRealMethod();

        PassengerResponse actualPassenger = passengerService.findPassengerById(passengerId);

        assertNotNull(actualPassenger);
        verify(passengerRepository).findById(passengerId);
        verify(ratingServiceClient).findAverageRating(passengerId, Role.PASSENGER.name());
        verify(passengerMapper).toPassengerResponse(passenger, averageRatingResponse);
    }

    @Test
    void findPassengerById_InvalidId_ThrowPassengerNotFoundException() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String exceptionMessage = String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_ID.getValue(), passengerId);

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.findPassengerById(passengerId))
                .isInstanceOf(PassengerNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void createPassenger_ValidPassengerRequest_PassengerSaved() {
        Long passengerId = TestConstants.PASSENGER_ID;
        PassengerRequest passengerRequest = TestUtils.defaultPassengerRequest();
        Passenger passenger = TestUtils.defaultPassenger();
        passenger.setId(passengerId);
        AverageRatingResponse averageRatingResponse = AverageRatingResponse.empty(passengerId);

        when(passengerMapper.toPassenger(any(PassengerRequest.class)))
                .thenReturn(passenger);
        when(passengerRepository.save(any(Passenger.class)))
                .thenReturn(passenger);
        when(passengerMapper.toPassengerResponse(any(Passenger.class), any(AverageRatingResponse.class)))
                .thenCallRealMethod();

        PassengerResponse savedPassenger = passengerService.savePassenger(passengerRequest);

        assertNotNull(savedPassenger);
        verify(passengerRepository).save(passenger);
        verify(passengerMapper).toPassenger(passengerRequest);
        verify(passengerMapper).toPassengerResponse(passenger, averageRatingResponse);
    }

    @Test
    void createPassenger_DuplicateEmail_ThrowUniqueConstraintViolationException() {
        PassengerRequest passengerRequest = TestUtils.defaultPassengerRequest();
        String exceptionMessage = String.format(MessageTemplates.EMAIL_NOT_UNIQUE.getValue(), TestConstants.PASSENGER_EMAIL);

        when(passengerRepository.existsByEmail(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> passengerService.savePassenger(passengerRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void createPassenger_DuplicatePhoneNumber_ThrowUniqueConstraintViolationException() {
        PassengerRequest passengerRequest = TestUtils.defaultPassengerRequest();
        String exceptionMessage =
                String.format(MessageTemplates.PHONE_NUMBER_NOT_UNIQUE.getValue(), TestConstants.PASSENGER_PHONE_NUMBER);

        when(passengerRepository.existsByPhoneNumber(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> passengerService.savePassenger(passengerRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void findAllPassengers_Success() {
        Long passengerId = TestConstants.PASSENGER_ID;
        List<Passenger> passengers = Collections.nCopies(3, TestUtils.defaultPassenger());
        List<AverageRatingResponse> averageRatingResponses =
                Collections.nCopies(3, AverageRatingResponse.empty(passengerId));
        AverageRatingListResponse averageRatingResponseList =
                TestUtils.averageRatingListResponse(averageRatingResponses, Role.PASSENGER);

        when(passengerRepository.findAll())
                .thenReturn(passengers);
        when(ratingServiceClient.findAllAverageRatings(anyString()))
                .thenReturn(averageRatingResponseList);
        when(passengerMapper.toPassengerListResponse(anyList(), anyList()))
                .thenCallRealMethod();

        PassengerListResponse actualPassengerList = passengerService.findAllPassengers();

        assertNotNull(actualPassengerList);
        assertEquals(passengers.size(), actualPassengerList.getQuantity());
    }

    @Test
    void updatePassenger_ValidPassengerRequestAndId_ReturnUpdatedPassenger() {
        Long passengerId = TestConstants.PASSENGER_ID;
        PassengerRequest passengerRequest = TestUtils.passengerRequestWithEmail(TestConstants.PASSENGER_UPDATED_EMAIL);
        Passenger passenger = TestUtils.defaultPassenger();
        AverageRatingResponse averageRatingResponse = AverageRatingResponse.empty(passengerId);

        when(passengerRepository.findById(passengerId))
                .thenReturn(Optional.of(passenger));
        doCallRealMethod().when(passengerMapper)
                .updatePassenger(any(PassengerRequest.class), any(Passenger.class));
        when(passengerRepository.save(any(Passenger.class)))
                .thenReturn(passenger);
        when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(averageRatingResponse);
        when(passengerMapper.toPassengerResponse(any(Passenger.class), any(AverageRatingResponse.class)))
                .thenCallRealMethod();

        PassengerResponse updatedPassenger = passengerService.updatePassenger(passengerId, passengerRequest);

        assertNotNull(updatedPassenger);
        assertEquals(TestConstants.PASSENGER_UPDATED_EMAIL, updatedPassenger.email());
        verify(passengerRepository).findById(passengerId);
        verify(passengerRepository).save(passenger);
        verify(passengerMapper).updatePassenger(passengerRequest, passenger);
        verify(passengerMapper).toPassengerResponse(passenger, averageRatingResponse);
        verify(ratingServiceClient).findAverageRating(passengerId, Role.PASSENGER.name());
    }

    @Test
    void updatePassenger_DuplicateEmail_ThrowUniqueConstraintViolationException() {
        String newEmail = TestConstants.PASSENGER_UPDATED_EMAIL;
        PassengerRequest passengerRequest = TestUtils.passengerRequestWithEmail(newEmail);
        Passenger passenger = TestUtils.defaultPassenger();
        String exceptionMessage = String.format(MessageTemplates.EMAIL_NOT_UNIQUE.getValue(), newEmail);

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.of(passenger));
        when(passengerRepository.existsByEmail(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> passengerService.updatePassenger(TestConstants.PASSENGER_ID, passengerRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);

    }

    @Test
    void updatePassenger_DuplicatePhoneNumber_ThrowUniqueConstraintViolationException() {
        String newPhoneNumber = TestConstants.PASSENGER_UPDATED_PHONE_NUMBER;
        PassengerRequest passengerRequest = TestUtils.passengerRequestWithPhoneNumber(newPhoneNumber);
        Passenger passenger = TestUtils.defaultPassenger();
        String exceptionMessage = String.format(MessageTemplates.PHONE_NUMBER_NOT_UNIQUE.getValue(), newPhoneNumber);

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.of(passenger));
        when(passengerRepository.existsByPhoneNumber(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> passengerService.updatePassenger(TestConstants.PASSENGER_ID, passengerRequest))
                .isInstanceOf(UniqueConstraintViolationException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void deletePassenger_ValidId_Success() {
        Long passengerId = TestConstants.PASSENGER_ID;
        Passenger passenger = TestUtils.defaultPassenger();

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.of(passenger));
        doNothing().when(passengerRepository)
                .delete(any(Passenger.class));

        assertDoesNotThrow(() -> passengerService.deletePassenger(passengerId));
        verify(passengerRepository).findById(passengerId);
        verify(passengerRepository).delete(passenger);
    }

    @Test
    void deletePassenger_InvalidId_ThrowPassengerNotFoundException() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String exceptionMessage = String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_ID.getValue(), passengerId);

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.deletePassenger(passengerId))
                .isInstanceOf(PassengerNotFoundException.class)
                .hasMessage(exceptionMessage);
    }
}