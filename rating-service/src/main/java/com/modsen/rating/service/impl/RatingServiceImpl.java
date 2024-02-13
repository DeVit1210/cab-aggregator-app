package com.modsen.rating.service.impl;

import com.modsen.rating.dto.request.PageSettingRequest;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.PagedRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.dto.response.RideResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.exception.IllegalRatingAttemptException;
import com.modsen.rating.exception.RatingAlreadyExistsException;
import com.modsen.rating.exception.RatingNotFoundException;
import com.modsen.rating.mapper.RatingMapper;
import com.modsen.rating.model.Rating;
import com.modsen.rating.repository.RatingRepository;
import com.modsen.rating.service.RatingService;
import com.modsen.rating.service.feign.DriverServiceClient;
import com.modsen.rating.service.feign.PassengerServiceClient;
import com.modsen.rating.service.feign.RideServiceClient;
import com.modsen.rating.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;
    private final RideServiceClient rideServiceClient;
    @Value("${rating.average.default}")
    private double defaultAverageRating;

    @Override
    public RatingResponse createRating(RatingRequest request) {
        validateRatingRequest(request);
        Rating rating = ratingMapper.toRating(request);
        Rating savedRating = ratingRepository.save(rating);

        return ratingMapper.toRatingResponse(savedRating);
    }

    @Override
    public RatingListResponse getAllRatings(Long ratedPersonId, Role role) {
        List<Rating> ratingList = ratingRepository.findAllByRoleAndRatedPersonId(role, ratedPersonId);
        if (ratingList.isEmpty()) {
            return new RatingListResponse(Collections.emptyList(), defaultAverageRating);
        }
        double averageRating = calculateAverageRating(ratingList);
        List<RatingResponse> ratingResponseList = ratingMapper.toRatingListResponse(ratingList);

        return new RatingListResponse(ratingResponseList, averageRating);
    }

    @Override
    public AverageRatingResponse getAverageRating(Long ratedPersonId, Role role) {
        List<Rating> ratingList = ratingRepository.findAllByRoleAndRatedPersonId(role, ratedPersonId);
        double averageRating = ratingList.isEmpty()
                ? defaultAverageRating
                : calculateAverageRating(ratingList);

        return AverageRatingResponse.builder()
                .ratedPersonId(ratedPersonId)
                .role(role)
                .averageRating(averageRating)
                .build();
    }

    @Override
    public PagedRatingResponse getRatings(Long ratedPersonId, Role role, PageSettingRequest request) {
        PageRequest pageRequest = PageRequestUtils.makePageRequest(request);
        Specification<Rating> searchSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("ratedPersonId"), ratedPersonId),
                        criteriaBuilder.equal(root.get("role"), role.name())
                );
        Page<Rating> ratingPage = ratingRepository.findAll(searchSpecification, pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, ratingPage);

        return ratingMapper.toPagedRatingResponse(ratingPage);
    }

    @Override
    public RatingResponse getRatingById(Long ratingId) {
        return ratingRepository.findById(ratingId)
                .map(ratingMapper::toRatingResponse)
                .orElseThrow(() -> new RatingNotFoundException(ratingId));
    }

    @Override
    public RatingResponse updateRating(Long ratingId, RatingValue ratingValue) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException(ratingId));
        rating.setRatingValue(ratingValue);
        Rating updatedRating = ratingRepository.save(rating);

        return ratingMapper.toRatingResponse(updatedRating);
    }

    private void validateRatingRequest(RatingRequest request) {
        Long rideId = request.getRideId();
        RideResponse rideResponse = validateRideId(rideId);

        Role role = Role.valueOf(request.getRole());
        if(role.equals(Role.DRIVER)) {
            validateDriverRating(rideResponse.driverId(), request.getRatedPersonId());
        } else {
            validatePassengerRating(rideResponse.passengerId(), request.getRatedPersonId());
        }

        if (ratingRepository.existsByRoleAndRideId(role, rideId)) {
            throw new RatingAlreadyExistsException(role, rideId);
        }
    }

    private double calculateAverageRating(List<Rating> ratingList) {
        return ratingList.stream()
                .map(Rating::getRatingValue)
                .collect(Collectors.averagingDouble(RatingValue::getValue));
    }

    private void validatePassengerRating(Long ratedPersonId, Long passengerId) {
        if(!ratedPersonId.equals(passengerId)) {
            throw new IllegalRatingAttemptException(ratedPersonId, Role.PASSENGER);
        }
        passengerServiceClient.findPassengerById(passengerId);
    }

    private void validateDriverRating(Long ratedPersonId, Long driverId) {
        if(!ratedPersonId.equals(driverId)) {
            throw new IllegalRatingAttemptException(ratedPersonId, Role.DRIVER);
        }
        driverServiceClient.findDriverById(driverId);
    }

    private RideResponse validateRideId(Long rideId) {
        return rideServiceClient.findRideById(rideId);
    }
}
