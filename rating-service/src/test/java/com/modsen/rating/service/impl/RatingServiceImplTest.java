package com.modsen.rating.service.impl;

import com.modsen.rating.constants.MessageTemplates;
import com.modsen.rating.constants.TestConstants;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingListResponse;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.dto.response.RideResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.exception.RatingAlreadyExistsException;
import com.modsen.rating.exception.RatingNotFoundException;
import com.modsen.rating.exception.base.NotFoundException;
import com.modsen.rating.mapper.RatingMapperImpl;
import com.modsen.rating.model.Rating;
import com.modsen.rating.repository.RatingRepository;
import com.modsen.rating.service.feign.DriverServiceClient;
import com.modsen.rating.service.feign.PassengerServiceClient;
import com.modsen.rating.service.feign.RideServiceClient;
import com.modsen.rating.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RatingMapperImpl ratingMapper;
    @Mock
    private DriverServiceClient driverServiceClient;
    @Mock
    private PassengerServiceClient passengerServiceClient;
    @Mock
    private RideServiceClient rideServiceClient;
    @InjectMocks
    private RatingServiceImpl ratingService;

    @Value("${rating.average.default}")
    private double defaultAverageRating;

    @Test
    void findRatingById_RatingExists_ReturnRating() {
        Long ratingId = TestConstants.RATING_ID;
        Rating rating = TestUtils.defaultRating(Role.PASSENGER);

        when(ratingRepository.findById(anyLong()))
                .thenReturn(Optional.of(rating));
        when(ratingMapper.toRatingResponse(any(Rating.class)))
                .thenCallRealMethod();

        RatingResponse actualRating = ratingService.getRatingById(ratingId);

        assertNotNull(actualRating);
        verify(ratingRepository).findById(ratingId);
        verify(ratingMapper).toRatingResponse(rating);
    }

    @Test
    void findRatingById_RatingDoesNotExist_ThrowRatingNotFoundException() {
        Long ratingId = TestConstants.RATING_ID;
        String exceptionMessage = String.format(MessageTemplates.RATING_NOT_FOUND.getValue(), ratingId);

        when(ratingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.getRatingById(ratingId))
                .isInstanceOf(RatingNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void getAllRatingsForPerson_ValidRoleAndPersonId_ReturnRatings() {
        List<RatingValue> ratingValues = List.of(RatingValue.THREE, RatingValue.FOUR, RatingValue.FIVE);
        List<Rating> ratingList = TestUtils.ratingForRoleAndRatingValues(Role.DRIVER, ratingValues);
        double expectedAverageRating = 4.00;

        when(ratingRepository.findAllByRoleAndRatedPersonId(any(Role.class), anyLong()))
                .thenReturn(ratingList);
        when(ratingMapper.toRatingListResponse(anyList()))
                .thenCallRealMethod();

        RatingListResponse actualRatingList = ratingService.getAllRatings(TestConstants.RATED_PERSON_ID, Role.DRIVER);

        assertEquals(ratingList.size(), actualRatingList.ratingList().size());
        assertEquals(expectedAverageRating, actualRatingList.averageRating());
        verify(ratingRepository).findAllByRoleAndRatedPersonId(Role.DRIVER, TestConstants.RATED_PERSON_ID);
        verify(ratingMapper).toRatingListResponse(ratingList);
    }

    @Test
    void getAllRatingsForPerson_NotFound_ReturnEmptyListWithDefaultAverageRating() {
        when(ratingRepository.findAllByRoleAndRatedPersonId(any(Role.class), anyLong()))
                .thenReturn(Collections.emptyList());

        RatingListResponse actualRatingList = ratingService.getAllRatings(TestConstants.RATED_PERSON_ID, Role.DRIVER);

        assertTrue(actualRatingList.ratingList().isEmpty());
        assertEquals(defaultAverageRating, actualRatingList.averageRating());
        verify(ratingRepository).findAllByRoleAndRatedPersonId(Role.DRIVER, TestConstants.RATED_PERSON_ID);
        verify(ratingMapper, never()).toRatingListResponse(anyList());
    }

    @Test
    void getAverageRatingForPerson_ValidRoleAndPersonId_ReturnAverageRating() {
        List<RatingValue> ratingValues = List.of(RatingValue.THREE, RatingValue.FOUR, RatingValue.FIVE);
        List<Rating> ratingList = TestUtils.ratingForRoleAndRatingValues(Role.PASSENGER, ratingValues);
        double expectedAverageRating = 4.00;

        when(ratingRepository.findAllByRoleAndRatedPersonId(any(Role.class), anyLong()))
                .thenReturn(ratingList);

        AverageRatingResponse actualAverageRating =
                ratingService.getAverageRating(TestConstants.RATED_PERSON_ID, Role.PASSENGER);

        assertNotNull(actualAverageRating);
        assertEquals(expectedAverageRating, actualAverageRating.averageRating());
        assertEquals(ratingValues.size(), actualAverageRating.ratesQuantity());
    }

    @Test
    void getAverageRatingForPerson_NotFound_ReturnDefaultAverageRating() {
        when(ratingRepository.findAllByRoleAndRatedPersonId(any(Role.class), anyLong()))
                .thenReturn(Collections.emptyList());

        AverageRatingResponse actualAverageRating =
                ratingService.getAverageRating(TestConstants.RATED_PERSON_ID, Role.PASSENGER);

        assertNotNull(actualAverageRating);
        assertEquals(defaultAverageRating, actualAverageRating.averageRating());
        assertEquals(0, actualAverageRating.ratesQuantity());
    }

    @Test
    void getAllAverageRatings_Success() {
        List<Long> ratedPersonIdList = List.of(1L, 2L, 3L);
        List<Rating> ratingList = TestUtils.ratingForRoleAndRatedPeople(Role.PASSENGER, ratedPersonIdList);

        when(ratingRepository.findAllByRole(any(Role.class)))
                .thenReturn(ratingList);

        AverageRatingListResponse allAverageRatings = ratingService.getAllAverageRatings(Role.PASSENGER);

        assertNotNull(allAverageRatings);
        assertEquals(ratedPersonIdList.size(), allAverageRatings.getQuantity());
    }

    @Test
    void createRating_ValidRatingRequest_ReturnCreatedRating() {
        Rating rating = TestUtils.defaultRating(Role.DRIVER);
        RatingRequest request = TestUtils.ratingRequestForRole(Role.DRIVER);
        RideResponse rideResponse = RideResponse.builder()
                .id(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .build();

        when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);
        when(ratingMapper.toRating(any(RatingRequest.class)))
                .thenReturn(rating);
        when(ratingRepository.save(any(Rating.class)))
                .thenReturn(rating);
        when(ratingMapper.toRatingResponse(any(Rating.class)))
                .thenCallRealMethod();

        RatingResponse createdRating = ratingService.createRating(request);

        assertNotNull(createdRating);
        verify(ratingMapper).toRating(request);
        verify(ratingRepository).save(rating);
        verify(ratingMapper).toRatingResponse(rating);
    }

    @Test
    void createRating_RideDoesNotExist_ThrowNotFoundException() {
        RatingRequest request = TestUtils.ratingRequestForRole(Role.DRIVER);

        when(rideServiceClient.findRideById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> ratingService.createRating(request))
                .isInstanceOf(NotFoundException.class);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void createRating_PassengerDoesNotExist_ThrowNotFoundException() {
        RatingRequest request = TestUtils.ratingRequestForRole(Role.PASSENGER);
        RideResponse rideResponse = RideResponse.builder()
                .passengerId(TestConstants.PASSENGER_ID)
                .build();

        when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);
        when(passengerServiceClient.findPassengerById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> ratingService.createRating(request))
                .isInstanceOf(NotFoundException.class);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void createRating_DriverDoesNotExist_ThrowNotFoundException() {
        RatingRequest request = TestUtils.ratingRequestForRole(Role.DRIVER);
        RideResponse rideResponse = RideResponse.builder()
                .driverId(TestConstants.DRIVER_ID)
                .build();

        when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);
        when(driverServiceClient.findDriverById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> ratingService.createRating(request))
                .isInstanceOf(NotFoundException.class);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void createRating_RatingAlreadyExistsForRideAndPerson_ThrowRatingAlreadyExistsException() {
        RatingRequest request = TestUtils.ratingRequestForRole(Role.DRIVER);
        RideResponse rideResponse = RideResponse.builder()
                .driverId(TestConstants.DRIVER_ID)
                .build();

        String exceptionMessage = String.format(
                MessageTemplates.RATING_ALREADY_EXISTS.getValue(),
                Role.DRIVER.name(),
                TestConstants.RATED_PERSON_ID
        );

        when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);
        when(ratingRepository.existsByRoleAndRideId(any(Role.class), anyLong()))
                .thenReturn(true);

        assertThatThrownBy(() -> ratingService.createRating(request))
                .isInstanceOf(RatingAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void updateRating_ValidRatingId_ReturnUpdatedRating() {
        Long ratingId = TestConstants.RATING_ID;
        RatingValue newRatingValue = RatingValue.THREE;
        Rating rating = TestUtils.defaultRating(Role.DRIVER);

        when(ratingRepository.findById(anyLong()))
                .thenReturn(Optional.of(rating));
        when(ratingRepository.save(any(Rating.class)))
                .thenReturn(rating);
        when(ratingMapper.toRatingResponse(any(Rating.class)))
                .thenCallRealMethod();

        RatingResponse updatedRating = ratingService.updateRating(ratingId, newRatingValue);

        assertNotNull(updatedRating);
        assertEquals(newRatingValue.ordinal(), updatedRating.ratingValue());
        verify(ratingRepository).findById(ratingId);
        verify(ratingRepository).save(rating);
        verify(ratingMapper).toRatingResponse(rating);
    }

    @Test
    void updateRating_RatingDoesNotExist_ThrowRatingNotFoundException() {
        Long ratingId = TestConstants.RATING_ID;
        String exceptionMessage = String.format(MessageTemplates.RATING_NOT_FOUND.getValue(), ratingId);

        when(ratingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.updateRating(ratingId, RatingValue.THREE))
                .isInstanceOf(RatingNotFoundException.class)
                .hasMessage(exceptionMessage);
    }
}